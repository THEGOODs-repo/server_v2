package thegoods.server.item.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import thegoods.server.common.enums.ItemStatus;
import thegoods.server.common.enums.types.DeliveryType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ItemResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadItemResultDTO {
        Long itemId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateItemResultDTO {
        Long itemId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemContentDTO {
        Long itemId;
        String name;
        String memberName;
        String memberProfileImgUrl;
        ItemStatus status;
        Integer stock;
        Long price;
        Integer deliveryFee;
        DeliveryType deliveryType;
        Integer deliveryDate;
        String description;
        Boolean isLimitless;
        LocalDate startDate;
        LocalDate endDate;
        String category;
        Long viewCount;
        Long dibsCount;
        Integer salesCount;
        List<ItemImgResponseDTO> itemImgUrlList;
        List<ItemOptionResponseDTO> itemOptionList;
        List<String> itemTag;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPreviewListDTO {
        List<ItemPreviewDTO> itemList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPreviewDTO {
        Long itemId;
        String name;
        ItemStatus status;
        LocalDate endDate;
        Long viewCount;
        String memberName;
        String memberProfileImgUrl;
        List<ItemImgResponseDTO> itemImgUrlList;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemImgResponseDTO {
        Boolean isThumbNail;
        String itemImgUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemOptionResponseDTO {
        Long itemOptionId;
        String name;
        Long price;
        Integer stock;
    }
}
