package thegoods.server.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class DataRequestDTO {
    @Getter
    public static class setItemDTO {
        @NotBlank
        String name;
        @NotNull
        String status;
        @Nullable
        Integer stock;
        @Nullable
        Long price;
        @NotNull
        Integer deliveryFee;
        @NotNull
        Integer deliveryType;
        @NotNull
        Integer deliveryDate;
        @NotNull
        String description;
        @NotNull
        Boolean isLimitless;
        @Nullable
        LocalDate startDate;
        @Nullable
        LocalDate endDate;
        @NotNull
        Long viewCount;
        @NotNull
        Long dibsCount;
        @NotNull
        Long salesCount;
        @NotNull
        Long category;
        @Nullable
        List<ItemRequestDTO.itemOptionDTO> itemOptionList;
        @NotNull
        List<String> itemTag;
        @NotNull
        String sellerName;
        @NotNull
        String itemUuid;
    }

    @Getter
    @AllArgsConstructor
    public static class setMemberDTO {
        String nickname;
        String uuid;
        List<Boolean> memberTerm;
        List<Long> memberCategory;

    }
}
