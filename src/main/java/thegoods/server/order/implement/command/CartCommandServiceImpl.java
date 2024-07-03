package thegoods.server.order.implement.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thegoods.server.common.enums.CartStatus;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.item.domain.Item;
import thegoods.server.item.domain.ItemOption;
import thegoods.server.item.exception.ItemHandler;
import thegoods.server.member.domain.Member;
import thegoods.server.order.converter.CartConverter;
import thegoods.server.order.domain.Cart;
import thegoods.server.order.domain.repository.CartRepository;
import thegoods.server.order.exception.OrderHandler;
import thegoods.server.order.presentation.dto.CartRequestDTO;
import thegoods.server.item.implement.query.ItemQueryService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartCommandServiceImpl implements CartCommandService {

    private final ItemQueryService itemQueryService;
    private final CartRepository cartRepository;

    @Override
    public void addCart(CartRequestDTO.cartAddDTOList request, Member member) {
        // item 존재 여부 검증은 annotation으로 진행
        request.getCartAddDTOList().forEach(cartAddDTO -> {
            Item item = itemQueryService.findItemById(cartAddDTO.getItemId()).get();

            if (!item.getItemOptionList().isEmpty()) { // 옵션이 있는 상품의 경우
                if (cartAddDTO.getItemOptionId() == null) {
                    throw new OrderHandler(ErrorStatus.NULL_ITEMOPTION_ERROR);
                }

                ItemOption itemOption = itemQueryService.findItemOptionById(cartAddDTO.getItemOptionId()).orElseThrow(() -> new ItemHandler(ErrorStatus.ITEMOPTION_NOT_FOUND));
                // itemOption이 해당 item의 것이 맞는지 검증
                if (!itemOption.getItem().equals(item)) {
                    throw new ItemHandler(ErrorStatus.ITEMOPTION_NOT_MATCH);
                }

                // 재고 수량과 비교
                if (cartAddDTO.getAmount() > itemOption.getStock()) {
                    throw new OrderHandler(ErrorStatus.LACK_OF_STOCK);
                }

                // cart 테이블에 해당 장바구니 내역 이미 존재하면, 해당 내역의 담은 수량만 업데이트
                Optional<Cart> cart = cartRepository.findByMemberAndItemAndItemOptionAndCartStatus(member, item, itemOption, CartStatus.ACTIVE);
                if (cart.isPresent()) { // cart 테이블에 해당 장바구니 내역 존재하는 경우
                    // 재고 수량과 비교
                    if (cartAddDTO.getAmount() + cart.get().getAmount() > itemOption.getStock()) {
                        throw new OrderHandler(ErrorStatus.LACK_OF_STOCK);
                    }
                    cart.get().updateAmount(cart.get().getAmount() + cartAddDTO.getAmount());

                } else { // cart 테이블에 해당 장바구니 내역 존재하지 않는 경우
                    // 장바구니 내역 새로 생성
                    Cart newCart = CartConverter.toCartWithItemOption(item, itemOption, cartAddDTO.getAmount());
                    newCart.setMember(member);
                    cartRepository.save(newCart);
                }

            } else { // 옵션이 없는 상품의 경우
                // 재고 수량과 비교
                if (cartAddDTO.getAmount() > item.getStock()) {
                    throw new OrderHandler(ErrorStatus.LACK_OF_STOCK);
                }

                Optional<Cart> cart = cartRepository.findByMemberAndItemAndCartStatus(member, item, CartStatus.ACTIVE);
                if (cart.isPresent()) { // cart 테이블에 해당 장바구니 내역 존재하는 경우
                    if (cartAddDTO.getAmount() + cart.get().getAmount() > item.getStock()) {
                        throw new OrderHandler(ErrorStatus.LACK_OF_STOCK);
                    }

                    cart.get().updateAmount(cartAddDTO.getAmount() + cart.get().getAmount());
                } else { // cart 테이블에 해당 장바구니 내역 존재하지 않는 경우
                    // 장바구니 내역 새로 생성
                    Cart newCart = CartConverter.toCartWithoutItemOption(item, cartAddDTO.getAmount());
                    newCart.setMember(member);
                    cartRepository.save(newCart);
                }
            }

        });
    }

    @Override
    public void updateCart(CartRequestDTO.cartUpdateDTOList request, Member member) {
        // cart 존재 여부 검증은 annotation에서 진행
        request.getCartUpdateDTOList().forEach(cartUpdateDTO -> {
            Cart cart = cartRepository.findById(cartUpdateDTO.getCartId()).get();

            // 해당 cart 내역을 수정할 권한 있는지 검증
            if (!cart.getMember().equals(member)) {
                throw new OrderHandler(ErrorStatus.NOT_CART_OWNER);
            }

            // 재고 수량과 비교
            if (!cart.getItem().getItemOptionList().isEmpty()) { // 상품 옵션이 있는 경우
                if (cartUpdateDTO.getAmount() > cart.getItemOption().getStock()) {
                    throw new OrderHandler(ErrorStatus.LACK_OF_STOCK);
                }
            } else {
                if (cartUpdateDTO.getAmount() > cart.getItem().getStock()) {
                    throw new OrderHandler(ErrorStatus.LACK_OF_STOCK);
                }
            }

            cart.updateAmount(cartUpdateDTO.getAmount());
        });
    }

    @Override
    public void deleteCart(CartRequestDTO.cartOptionDeleteDTO request, Member member) {

        request.getCartIdList().forEach(cartId -> {
            Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new OrderHandler(ErrorStatus.CART_NOT_FOUND));

            // 해당 cart 내역을 수정할 권한 있는지 검증
            if (!cart.getMember().equals(member)) {
                throw new OrderHandler(ErrorStatus.NOT_CART_OWNER);
            }

            cart.setCartStatus(CartStatus.USER_DEL);
        });
    }

    @Override
    public void deleteCartByItem(CartRequestDTO.cartDeleteByItemDTO request, Member member) {
        request.getItemIdList().forEach(itemId -> {
            List<Cart> itemCartList = cartRepository.findAllByMemberAndItemIdAndCartStatus(member, itemId, CartStatus.ACTIVE);

            if (itemCartList.isEmpty()) {
                throw new OrderHandler(ErrorStatus.CART_NOT_FOUND);
            }

            itemCartList.forEach(cart -> {
                cart.setCartStatus(CartStatus.USER_DEL);
            });
        });
    }
}
