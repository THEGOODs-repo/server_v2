package thegoods.server.item.presentation.dto;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReviewRequestDTO {

    @Getter
    public static class addReviewDTO {
        @NotNull
        @Min(1)
        @Max(5)
        Integer score;

        @NotNull
        Long orderItemId;

        @Size(max = 1500)
        String text;
    }
}
