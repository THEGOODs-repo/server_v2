package thegoods.server.item.implement.command;

import thegoods.server.item.domain.Review;
import thegoods.server.item.presentation.dto.ReviewRequestDTO;
import thegoods.server.member.domain.Member;

public interface ReviewCommandService {

    Review create(ReviewRequestDTO.addReviewDTO request, Member member);
}
