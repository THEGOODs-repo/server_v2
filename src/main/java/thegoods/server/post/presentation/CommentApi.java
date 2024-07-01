package thegoods.server.post.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
@Tag(name = "Comment", description = "댓글 관련 API 입니다.")
public class CommentApi {

    final CommentService commentService;
    final MemberService memberService;
    final PostService postService;


}
