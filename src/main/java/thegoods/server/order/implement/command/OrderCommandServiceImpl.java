package thegoods.server.order.implement.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thegoods.server.common.enums.CartStatus;
import thegoods.server.common.enums.OrderStatus;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.member.domain.Member;
import thegoods.server.order.converter.OrderConverter;
import thegoods.server.order.domain.Cart;
import thegoods.server.order.domain.OrderDetail;
import thegoods.server.order.domain.OrderItem;
import thegoods.server.order.domain.Orders;
import thegoods.server.order.domain.repository.CartRepository;
import thegoods.server.order.domain.repository.OrderItemRepository;
import thegoods.server.order.domain.repository.OrderRepository;
import thegoods.server.order.presentation.dto.OrderRequestDTO;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandServiceImpl implements OrderCommandService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final CartRepository cartRepository;

    @Override
    public Orders create(OrderRequestDTO.OrderAddDTO request, Member member) {
        // item 및 itemOption의 재고와 주문 수량 비교
        request.getOrderItemDTOList().forEach(orderItemDto -> {
            Item item = itemRepository.findById(orderItemDto.getItemId()).get();

            orderItemDto.getOrderDetailDTOList().forEach(orderDetailDTO -> {
                if (orderDetailDTO.getItemOptionId() != null) { // 옵션이 있는 상품인 경우
                    ItemOption itemOption = itemOptionRepository.findById(orderDetailDTO.getItemOptionId()).get();
                    if (orderDetailDTO.getAmount() > itemOption.getStock()) { // 재고보다 주문 수량이 많은 경우
                        throw new OrderHandler(ErrorStatus.LACK_OF_STOCK);
                    }
                } else { // 단일 상품인 경우
                    if (orderDetailDTO.getAmount() > item.getStock()) {
                        throw new OrderHandler(ErrorStatus.LACK_OF_STOCK);
                    }
                }
            });
        });

        // Orders 엔티티 생성 및 연관관계 매핑
        Orders newOrders = OrderConverter.toOrders(request);
        newOrders.setMember(member);
        Orders orders = orderRepository.save(newOrders);

        // OrderItem, OrderDetail 엔티티 생성, 연관관계 매핑, 판매 재고 업데이트
        request.getOrderItemDTOList().forEach(orderItemDto -> {
            Item item = itemRepository.findById(orderItemDto.getItemId()).get();

            // OrderItem 엔티티 생성 및 연관관계 매핑
            OrderItem newOrderItem = OrderConverter.toOrderItem(request, item.getDeliveryFee());
            newOrderItem.setItem(item);
            newOrderItem.setOrders(orders);
            OrderItem orderItem = orderItemRepository.save(newOrderItem);

            orderItemDto.getOrderDetailDTOList().forEach(orderDetailDTO -> {
                if (orderDetailDTO.getItemOptionId() != null) { // 옵션이 있는 상품인 경우
                    ItemOption itemOption = itemOptionRepository.findById(orderDetailDTO.getItemOptionId()).get();

                    // OrderDetail 엔티티 생성
                    OrderDetail orderDetail = OrderConverter.toOrderDetail(orderDetailDTO, itemOption.getPrice());
                    orderDetail.setOrderItem(orderItem);
                    orderDetail.setItemOption(itemOption);

                    // item, itemOption의 판매량, 재고 업데이트
                    item.updateSales(orderDetail.getAmount());
                    itemOption.updateStock(-orderDetail.getAmount());

                    // OrderItem 주문 상품 합산 금액 업데이트
                    orderItem.updateTotalPrice(orderDetail.getOrderPrice());
                } else {
                    OrderDetail orderDetail = OrderConverter.toOrderDetail(orderDetailDTO, item.getPrice());
                    orderDetail.setOrderItem(orderItem);

                    // item의 판매량, 재고 업데이트
                    item.updateSales(orderDetail.getAmount());
                    item.updateStock(-orderDetail.getAmount());

                    // OrderItem 주문 상품 합산 금액 업데이트
                    orderItem.updateTotalPrice(orderDetail.getOrderPrice());
                }

                // 장바구니에서 주문 요청하는 경우, cartId에 해당하는 장바구니 내역 삭제
                if (orderDetailDTO.getCartId() != null) {
                    Cart cart = cartRepository.findById(orderDetailDTO.getCartId()).orElseThrow(() -> new OrderHandler(ErrorStatus.CART_NOT_FOUND));

                    // 해당 cart 내역을 수정할 권한 있는지 검증
                    if (!cart.getMember().equals(member)) {
                        throw new OrderHandler(ErrorStatus.NOT_CART_OWNER);
                    }

                    cart.setCartStatus(CartStatus.USER_DEL);
                }
            });
            orderItem.updateTotalPrice(Long.valueOf(item.getDeliveryFee()));


        });

        return orders;
    }

    @Override
    public OrderItem updateStatusToConfirm(Long orderItemId, Member member) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new OrderHandler(ErrorStatus.ORDER_ITEM_NOT_FOUND));

        // 해당 orderItem을 수정할 권한이 있는지 검증
        if (!orderItem.getOrders().getMember().equals(member)) {
            throw new OrderHandler(ErrorStatus.NOT_ORDER_OWNER);
        }

        // 해당 orderItem의 상태가 DEL_COMP인지 검증
        if (orderItem.getStatus() != OrderStatus.DEL_COMP) {
            throw new OrderHandler(ErrorStatus.ORDER_ITEM_UPDATE_FAIL);
        }

        return orderItem.updateStatus(OrderStatus.CONFIRM);
    }

    @Override
    public OrderItem updateOrderItemAddress(OrderRequestDTO.OrderItemAddressUpdateDTO request, Long orderItemId, Member member) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new OrderHandler(ErrorStatus.ORDER_ITEM_NOT_FOUND));

        // member가 해당 orderItem에 수정 권한이 있는지 검증
        if (!orderItem.getOrders().getMember().equals(member)) {
            throw new OrderHandler(ErrorStatus.NOT_ORDER_OWNER);
        }

        // 해당 orderItem의 status가 배송 준비 이전인지 검증
        if (!(orderItem.getStatus() == OrderStatus.PAY_PREV || orderItem.getStatus() == OrderStatus.PAY_COMP)) {
            throw new OrderHandler(ErrorStatus.ORDER_ITEM_UPDATE_FAIL);
        }

        return orderItem.updateAddressInfo(request);
    }

    @Override
    public OrderItem updateOrderItemRefundInfo(OrderRequestDTO.OrderItemRefundInfoUpdateDTO request, Long orderItemId, Member member) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new OrderHandler(ErrorStatus.ORDER_ITEM_NOT_FOUND));

        // member가 해당 orderItem에 수정 권한이 있는지 검증
        if (!orderItem.getOrders().getMember().equals(member)) {
            throw new OrderHandler(ErrorStatus.NOT_ORDER_OWNER);
        }

        // orderItem의 status가 환불 계좌 변경 가능한 단계인지 검증
        if (!(orderItem.getStatus() == OrderStatus.PAY_PREV
                || orderItem.getStatus() == OrderStatus.PAY_COMP
                || orderItem.getStatus() == OrderStatus.DEL_PREP
                || orderItem.getStatus() == OrderStatus.DEL_START
                || orderItem.getStatus() == OrderStatus.DEL_COMP
        )) {
            throw new OrderHandler(ErrorStatus.ORDER_ITEM_UPDATE_FAIL);
        }

        return orderItem.updateRefundInfo(request);
    }
}
