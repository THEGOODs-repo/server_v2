package thegoods.server.item.implement.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.item.converter.ReviewConverter;
import thegoods.server.item.domain.Review;
import thegoods.server.item.domain.repository.ReviewRepository;
import thegoods.server.item.exception.ReviewHandler;
import thegoods.server.item.presentation.dto.ReviewRequestDTO;
import thegoods.server.member.domain.Member;
import thegoods.server.order.domain.OrderItem;
import thegoods.server.order.domain.repository.OrderItemRepository;
import thegoods.server.order.exception.OrderHandler;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 등록
     *
     * @param request
     * @param member
     * @return
     */
    @Override
    public Review create(ReviewRequestDTO.addReviewDTO request, Member member) {
        // 해당 orderItem이 해당 회원의 것이 맞는지 검증
        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId()).orElseThrow(() -> new OrderHandler(ErrorStatus.ORDER_ITEM_NOT_FOUND));
        if (!orderItem.getOrders().getMember().equals(member)) {
            throw new ReviewHandler(ErrorStatus.NOT_ORDER_OWNER);
        }

        // 이미 리뷰를 등록했던 orderItem인지 검증
        boolean isExists = reviewRepository.existsByOrderItem(orderItem);
        if (isExists) {
            throw new ReviewHandler(ErrorStatus.REVIEW_ALREADY_EXISTS);
        }

        // review 엔티티 생성 및 연관관계 매핑
        Review review = ReviewConverter.toReview(orderItem, request.getText(), request.getScore());
        review.setMember(member);
        review.setItem(orderItem.getItem());

        return reviewRepository.save(review);
    }
}

