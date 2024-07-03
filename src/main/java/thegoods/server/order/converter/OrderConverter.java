package thegoods.server.order.converter;

import org.springframework.data.domain.Page;
import thegoods.server.common.enums.OrderStatus;
import thegoods.server.common.enums.types.PayType;
import thegoods.server.item.converter.ItemConverter;
import thegoods.server.order.domain.OrderDetail;
import thegoods.server.order.domain.OrderItem;
import thegoods.server.order.domain.Orders;
import thegoods.server.item.presentation.dto.ItemResponseDTO;
import thegoods.server.order.presentation.dto.OrderRequestDTO;
import thegoods.server.order.presentation.dto.OrderResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderConverter {

    public static OrderResponseDTO.OrderAddResultDTO toOrderAddResultDto(Orders order) {
        return OrderResponseDTO.OrderAddResultDTO.builder()
                .orderId(order.getId())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public static Orders toOrders(OrderRequestDTO.OrderAddDTO request) {
        PayType payType = null;
        switch (request.getPayType()) {
            case "CARD":
                payType = PayType.CARD;
                break;
            case "ACCOUNT":
                payType = PayType.ACCOUNT;
                break;
        }

        return Orders.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .payType(payType)
                .orderItemList(new ArrayList<>())
                .build();
    }

    public static OrderItem toOrderItem(OrderRequestDTO.OrderAddDTO request, Integer deliveryFee) {
        return OrderItem.builder()
                .totalPrice(0L)
                .deliveryFee(deliveryFee)
                .status(OrderStatus.PAY_COMP)
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .deliveryMemo(request.getDeliveryMemo())
                .refundBank(request.getRefundBank())
                .refundAccount(request.getRefundAccount())
                .refundOwner(request.getRefundOwner())
                .depositor(request.getDepositor())
                .orderDetailList(new ArrayList<>())
                .build();
    }

    public static OrderDetail toOrderDetail(OrderRequestDTO.OrderDetailDTO orderDetailDTO, Long price) {
        return OrderDetail.builder()
                .amount(orderDetailDTO.getAmount())
                .orderPrice(orderDetailDTO.getAmount() * price)
                .build();
    }

    public static OrderResponseDTO.OrderItemPreViewDTO toOrderItemPreViewDTO(OrderItem orderItem) {

        List<ItemResponseDTO.ItemImgResponseDTO> itemImgResponseDTOList = orderItem.getItem().getItemImgList().stream()
                .map(ItemConverter::getItemImgDTO)
                .filter(ItemResponseDTO.ItemImgResponseDTO::getIsThumbNail).collect(Collectors.toList());

        String itemImgUrl = itemImgResponseDTOList.get(0).getItemImgUrl();

        return OrderResponseDTO.OrderItemPreViewDTO.builder()
                .orderItemId((orderItem.getId()))
                .orderStatus(orderItem.getStatus())
                .orderDateTime(orderItem.getCreatedAt())
                .itemName(orderItem.getItem().getName())
                .optionString(toOptionString(orderItem))
                .imgUrl(itemImgUrl)
                .price(orderItem.getTotalPrice())
                .build();
    }

    public static OrderResponseDTO.orderItemPreViewListDTO toOrderPreViewListDTO(Page<OrderItem> orderItemList) {
        List<OrderResponseDTO.OrderItemPreViewDTO> orderItemPreViewDTOList = orderItemList.stream()
                .map(OrderConverter::toOrderItemPreViewDTO).collect(Collectors.toList());

        return OrderResponseDTO.orderItemPreViewListDTO.builder()
                .isLast(orderItemList.isLast())
                .isFirst(orderItemList.isFirst())
                .totalPage(orderItemList.getTotalPages())
                .totalElements(orderItemList.getTotalElements())
                .listSize(orderItemPreViewDTOList.size())
                .orderItemPreViewDTOList(orderItemPreViewDTOList)
                .build();
    }

    public static OrderResponseDTO.OrderItemViewDTO toOrderItemViewDTO(OrderItem orderItem) {

        List<ItemResponseDTO.ItemImgResponseDTO> itemImgResponseDTOList = orderItem.getItem().getItemImgList().stream()
                .map(ItemConverter::getItemImgDTO)
                .filter(ItemResponseDTO.ItemImgResponseDTO::getIsThumbNail).collect(Collectors.toList());

        String itemImgUrl = itemImgResponseDTOList.get(0).getItemImgUrl();

        return OrderResponseDTO.OrderItemViewDTO.builder()
                .ordersId(orderItem.getOrders().getId())
                .orderItemId(orderItem.getId())
                .imgUrl(itemImgUrl)
                .itemName(orderItem.getItem().getName())
                .optionString(toOptionString(orderItem))
                .orderStatus(orderItem.getStatus())
                .buyerInfoDTO(toBuyerInfoDTO(orderItem))
                .orderItemInfoDTO(toOrderItemInfoDTO(orderItem))
                .depositInfoDTO(toDepositInfoDTO(orderItem))
                .addressInfoDTO(toAddressInfoDTO(orderItem))
                .deliveryInfoDTO(toDeliveryInfoDTO(orderItem))
                .refundInfoDTO(toRefundInfoDTO(orderItem))
                .build();

    }

    public static OrderResponseDTO.BuyerInfoDTO toBuyerInfoDTO(OrderItem orderItem) {
        return OrderResponseDTO.BuyerInfoDTO.builder()
                .name(orderItem.getOrders().getName())
                .phone(orderItem.getOrders().getPhone())
                .email((orderItem.getOrders().getMember().getNickname() == "no_login_user") ? null : orderItem.getOrders().getMember().getEmail()) // 비회원 주문인 경우 null값 넣음
                .build();
    }

    public static OrderResponseDTO.OrderItemInfoDTO toOrderItemInfoDTO(OrderItem orderItem) {
        List<OrderResponseDTO.OrderDetailInfoDTO> orderDetailInfoDTOList = orderItem.getOrderDetailList().stream()
                .map(OrderConverter::toOrderDetailInfoDTO).collect(Collectors.toList());

        return OrderResponseDTO.OrderItemInfoDTO.builder()
                .orderDateTime(orderItem.getOrders().getCreatedAt())
                .itemPrice(orderItem.getTotalPrice() - orderItem.getDeliveryFee())
                .deliveryFee(orderItem.getDeliveryFee())
                .totalPrice(orderItem.getTotalPrice())
                .deliveryNum(orderItem.getDeliveryNum())
                .orderDetailInfoDTOList(orderDetailInfoDTOList)
                .build();

    }

    public static OrderResponseDTO.OrderDetailInfoDTO toOrderDetailInfoDTO(OrderDetail orderDetail) {
        return OrderResponseDTO.OrderDetailInfoDTO.builder()
                .optionName(orderDetail.getItemOption() == null ? null : orderDetail.getItemOption().getName())
                .amount(orderDetail.getAmount())
                .price(orderDetail.getOrderPrice())
                .build();
    }

    public static OrderResponseDTO.DepositInfoDTO toDepositInfoDTO(OrderItem orderItem) {
        return OrderResponseDTO.DepositInfoDTO.builder()
                .depositor(orderItem.getDepositor())
                .depositDate(orderItem.getDepositDate())
                .depositAmount(orderItem.getTotalPrice())
                .build();
    }

    public static OrderResponseDTO.AddressInfoDTO toAddressInfoDTO(OrderItem orderItem) {
        return OrderResponseDTO.AddressInfoDTO.builder()
                .deliveryType(orderItem.getItem().getDeliveryType())
                .name(orderItem.getReceiverName())
                .phone(orderItem.getReceiverPhone())
                .zipcode(orderItem.getZipcode())
                .address(orderItem.getAddress() + " " + orderItem.getAddressDetail())
                .deliveryMemo(orderItem.getDeliveryMemo())
                .build();
    }

    public static OrderResponseDTO.DeliveryInfoDTO toDeliveryInfoDTO(OrderItem orderItem) {
        return OrderResponseDTO.DeliveryInfoDTO.builder()
                .deliveryComp(orderItem.getDeliveryComp())
                .deliveryNum(orderItem.getDeliveryNum())
                .build();
    }

    public static OrderResponseDTO.RefundInfoDTO toRefundInfoDTO(OrderItem orderItem) {
        return OrderResponseDTO.RefundInfoDTO.builder()
                .refundOwner(orderItem.getRefundOwner())
                .refundBank(orderItem.getRefundBank())
                .refundAccount(orderItem.getRefundAccount())
                .build();
    }

    // optionString 생성 메소드
    public static String toOptionString(OrderItem orderItem) {
        Boolean hasOption = (!orderItem.getItem().getItemOptionList().isEmpty()); // 옵션 유무

        Integer orderDetailCount = orderItem.getOrderDetailList().size();

        String itemOptionName;
        if (hasOption) { // 해당 주문 상품에 옵션이 있는 경우
            itemOptionName = orderItem.getOrderDetailList().get(0).getItemOption().getName(); // 대표 옵션 명 설정
            itemOptionName += orderDetailCount > 1 ? " 외 " + orderDetailCount.toString() + "건" : " 1건"; // optionString 설정
        } else {
            itemOptionName = null;
        }

        return itemOptionName;
    }

    public static OrderResponseDTO.OrderItemUpdateResultDTO toOrderItemUpdateResultDTO(OrderItem orderItem) {
        return OrderResponseDTO.OrderItemUpdateResultDTO.builder()
                .orderItemId(orderItem.getId())
                .updatedAt(orderItem.getUpdatedAt())
                .build();
    }
}
