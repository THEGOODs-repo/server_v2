package thegoods.server.item.converter;

import thegoods.server.common.enums.ReviewStatus;
import thegoods.server.item.domain.Review;
import thegoods.server.item.presentation.dto.ReviewResponseDTO;
import thegoods.server.order.domain.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class ReviewConverter {

    public static Review toReview(OrderItem orderItem, String text, Integer score) {
        return Review.builder()
                .text(text)
                .score(score)
                .orderItem(orderItem)
                .status(ReviewStatus.SHOW)
                .build();
    }

    public static ReviewResponseDTO.reviewPostDTO toReviewPostDTO(Review review) {
        return ReviewResponseDTO.reviewPostDTO.builder()
                .reviewId(review.getId())
                .createdAt(review.getCreatedAt())
                .itemName(review.getItem().getName())
                .score(review.getScore())
                .text(review.getText())
                .optionStringList(ReviewConverter.toOptionStringList(review.getOrderItem()))
                .build();
    }

    private static List<String> toOptionStringList(OrderItem orderItem) {
        return orderItem.getOrderDetailList().stream().map(orderDetail -> {
            return orderDetail.getItemOption().getName();
        }).collect(Collectors.toList());
    }
}

