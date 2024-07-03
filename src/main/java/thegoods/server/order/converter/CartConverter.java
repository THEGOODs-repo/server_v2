package thegoods.server.order.converter;

import thegoods.server.common.enums.CartStatus;
import thegoods.server.item.converter.ItemConverter;
import thegoods.server.item.presentation.dto.ItemResponseDTO;
import thegoods.server.order.domain.Cart;
import thegoods.server.order.presentation.dto.CartResponseDTO;
import thegoods.server.item.domain.Item;
import thegoods.server.item.domain.ItemOption;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartConverter {
    public static Cart toCartWithItemOption(Item item, ItemOption itemOption, Integer amount) {
        return Cart.builder()
                .item(item)
                .itemOption(itemOption)
                .amount(amount)
                .cartStatus(CartStatus.ACTIVE)
                .build();
    }

    public static Cart toCartWithoutItemOption(Item item, Integer amount) {
        return Cart.builder()
                .item(item)
                .amount(amount)
                .cartStatus(CartStatus.ACTIVE)
                .build();
    }


    public static CartResponseDTO.cartViewListDTO toCartViewListDTO(List<Cart> cartList) {

        List<CartResponseDTO.cartViewDTO> cartViewDTOList = new ArrayList<>();

        cartList.forEach(cart -> {
            if (!cartViewDTOList.isEmpty()) {
                // cartViewDTOList의 마지막 DTO와 cart를 비교해 cartViewDTO를 새로 추가할지, 기존 cartViewDTO 내에 cartOptionViewDTO만 추가할지 결정
                CartResponseDTO.cartViewDTO cartViewDTO = cartViewDTOList.get(cartViewDTOList.size() - 1);
                if (cartViewDTO.getItemId().equals(cart.getItem().getId())) { // cartViewDTO 내의 cartOptionViewDTO 추가
                    cartViewDTO.addCartOptionViewDTO(toCartOptionViewDTO(cart));
                } else { // cartViewDTOList에 새로운 cartViewDTO 생성 및 추가
                    CartResponseDTO.cartViewDTO newCartViewDTO = toCartViewDTO(cart);
                    cartViewDTOList.add(newCartViewDTO);
                }
            } else { // 첫 cartViewDTO 추가
                CartResponseDTO.cartViewDTO newCartViewDTO = toCartViewDTO(cart);
                cartViewDTOList.add(newCartViewDTO);
            }

        });

        return CartResponseDTO.cartViewListDTO.builder()
                .cartViewDTOList(cartViewDTOList)
                .build();
    }

    public static CartResponseDTO.cartViewDTO toCartViewDTO(Cart cart) {

        List<CartResponseDTO.cartOptionViewDTO> cartOptionViewDTOList = new ArrayList<>();
        cartOptionViewDTOList.add(toCartOptionViewDTO(cart));

        List<ItemResponseDTO.ItemImgResponseDTO> itemImgResponseDTOList = cart.getItem().getItemImgList().stream()
                .map(ItemConverter::getItemImgDTO)
                .filter(ItemResponseDTO.ItemImgResponseDTO::getIsThumbNail).collect(Collectors.toList());

        String itemImgUrl = itemImgResponseDTOList.get(0).getItemImgUrl();

        return CartResponseDTO.cartViewDTO.builder()
                .sellerName(cart.getItem().getMember().getNickname())
                .itemId(cart.getItem().getId())
                .itemName(cart.getItem().getName())
                .itemImg(itemImgUrl)
                .deliveryFee(cart.getItem().getDeliveryFee())
                .cartOptionViewDTOList(cartOptionViewDTOList)
                .build();
    }

    public static CartResponseDTO.cartOptionViewDTO toCartOptionViewDTO(Cart cart) {
        return CartResponseDTO.cartOptionViewDTO.builder()
                .cartId(cart.getId())
                .optionId(cart.getItemOption() == null ? null : cart.getItemOption().getId())
                .optionName(cart.getItemOption() == null ? null : cart.getItemOption().getName()) // 옵션이 있는 경우에만 옵션 이름 설정
                .price(cart.getItemOption() == null ? cart.getItem().getPrice() : cart.getItemOption().getPrice())
                .amount(cart.getAmount())
                .build();
    }

    public static CartResponseDTO.cartStockDTO toCartStockDTO(List<Cart> cartList) {

        List<CartResponseDTO.cartOptionStockDTO> cartOptionStockDTOList = cartList.stream()
                .map(CartConverter::toCartOptionStockDTO).collect(Collectors.toList());


        return CartResponseDTO.cartStockDTO.builder()
                .itemId(cartList.get(0).getItem().getId())
                .cartOptionStockDTOList(cartOptionStockDTOList)
                .build();

    }

    public static CartResponseDTO.cartOptionStockDTO toCartOptionStockDTO(Cart cart) {
        return CartResponseDTO.cartOptionStockDTO.builder()
                .itemOptionId(cart.getItemOption() == null ? null : cart.getItemOption().getId())
                .stock(cart.getItemOption() == null ? cart.getItem().getStock() : cart.getItemOption().getStock())
                .build();
    }
}
