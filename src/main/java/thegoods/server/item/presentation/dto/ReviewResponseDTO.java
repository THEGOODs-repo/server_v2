package thegoods.server.item.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reviewPostDTO {
        Long reviewId;
        LocalDateTime createdAt;
        String itemName;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<String> optionStringList;
        Integer score;
        String text;
    }
}
