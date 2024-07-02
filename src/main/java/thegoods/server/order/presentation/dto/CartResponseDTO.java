package thegoods.server.order.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CartResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class cartViewListDTO {
        List<cartViewDTO> cartViewDTOList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class cartViewDTO {
        String sellerName;
        Long itemId;
        String itemName;
        String itemImg;
        Integer deliveryFee;
        List<cartOptionViewDTO> cartOptionViewDTOList;

        public void addCartOptionViewDTO(cartOptionViewDTO cartOptionViewDTO) {
            this.cartOptionViewDTOList.add(cartOptionViewDTO);
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class cartOptionViewDTO {
        Long cartId;
        Long optionId;
        String optionName;
        Long price;
        Integer amount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class cartStockDTO {
        Long itemId;
        List<cartOptionStockDTO> cartOptionStockDTOList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class cartOptionStockDTO {
        Long itemOptionId;
        Integer stock;
    }
}
