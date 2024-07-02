package thegoods.server.post.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.exception.status.SuccessStatus;
import thegoods.server.common.presentation.ApiResponse;
import thegoods.server.member.domain.Member;
import thegoods.server.post.implement.command.PostCommandService;
import thegoods.server.post.implement.query.PostQueryService;
import thegoods.server.post.presentation.dto.PostRequestDTO;
import thegoods.server.post.presentation.dto.PostResponseDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Tag(name = "Post", description = "포스트 관련된 API 입니다.")
public class PostApi {

    private final PostQueryService postQueryService;
    private final MemberQueryService memberQueryService;
    final PostCommandService postCommandService;

    @GetMapping("/")
    @Operation(summary = "인기 사장님 피드 전체 조회 API", description = "포스트의 기본 디폴트 정렬로, 좋아요 순으로 내림차순 정렬합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON404", description = "Fail, 실패")
    })
    public ApiResponse<PostResponseDTO.PostListViewDTO> getPostByLikes() {
        List<PostResponseDTO> posts = postQueryService.getAllPostsSortedByLikes();
        return ApiResponse.of(SuccessStatus.POST_GET_SUCCESS, new PostResponseDTO.PostListViewDTO(posts));
    }

    @GetMapping("/following/all")
    @Operation(summary = "팔로잉 중인 피드 전체 조회 API")
    public ApiResponse<PostResponseDTO.PostListViewDTO> following(@RequestParam("like") boolean like) {
        return null;
    }

    @GetMapping("/{postId}")
    @Operation(summary = "피드 상세 조회 API", description = "조회 하고자 하는 postId 값이 필요합니다.")
    public ApiResponse<PostResponseDTO.PostViewDTO> post(@PathVariable Long postId) {
        return null;
    }

    @PostMapping("/follow/{followingId}")
    @Operation(summary = "팔로우 API", description = "팔로우 하려는 사람의 id를 request로 주시면 됩니다.")
    public ApiResponse<?> follow(Authentication authentication,
                                 @PathVariable(name = "followingId") Long followingId) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        //팔로우 기능
        postCommandService.follow(followingId, member);

        return ApiResponse.of(SuccessStatus.POST_FOLLOW_SUCCESS, null);
    }

    @PostMapping("/follow/{followingId}/delete")
    @Operation(summary = "팔로우 취소 API", description = "팔로우 취소하려는 사람의 id를 request로 주시면 됩니다.")
    public ApiResponse<?> deleteFollow(Authentication authentication,
                                       @PathVariable(name = "followingId") Long followingId) {

        Long memberId = Long.valueOf(authentication.getName().toString());
        //팔로우 취소 기능
        postCommandService.deleteFollow(followingId, memberId);
        return ApiResponse.of(SuccessStatus.POST_DELETE_FOLLOW_SUCCESS, null);
    }

    @PostMapping("/")
    @Operation(summary = "피드 등록 API", description = "content에 feed 내용, postImgList에 배열에 이미지를 담아서 넘겨주면 됩니다.")
    public ApiResponse<?> registerPost(@RequestPart(value = "content") String content,
                                       @RequestPart(value = "postImgList", required = false) List<MultipartFile> postImgList,
                                       Authentication authentication) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postCommandService.registerPost(member, content, postImgList);

        return ApiResponse.of(SuccessStatus.POST_UPLOAD_SUCCESS, null);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "피드 수정 API", description = "postId는 수정할 피드 id, content에 feed 내용, postImgList에 배열에 이미지를 담아서 넘겨주면 됩니다.")
    public ApiResponse<PostResponseDTO.PostStatusDTO> updatePost(@PathVariable(name = "postId") Long postId,
                                                                 @RequestPart(value = "content") String content,
                                                                 @RequestPart(value = "postImgList", required = false) List<MultipartFile> postImgList,
                                                                 Authentication authentication) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postCommandService.updatePost(member, postId, content, postImgList);

        return ApiResponse.of(SuccessStatus.POST_UPDATE_SUCCESS, null);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "피드 삭제 API", description = "postId는 수정할 피드 id, content에 feed 내용, postImgList에 배열에 이미지를 담아서 넘겨주면 됩니다.")
    public ApiResponse<PostResponseDTO.PostStatusDTO> deletePost(@PathVariable(name = "postId") Long postId,
                                                                 Authentication authentication) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postCommandService.deletePost(member, postId);

        return ApiResponse.of(SuccessStatus.POST_DELETE_SUCCESS, null);
    }

    @PostMapping("/{postId}/likes")
    @Operation(summary = "피드 좋아요 API", description = "postId: 좋아요 누를 피드 id, 한번 누르면 좋아요 두번 누르면 좋아요 취소")
    public ApiResponse<?> likePost(@PathVariable(name = "postId") Long postId,
                                   Authentication authentication) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postCommandService.likePost(member, postId);
        return ApiResponse.of(SuccessStatus.POST_LIKE_SUCCESS, null);
    }


    @PostMapping("/comment/{commentId}/likes")
    @Operation(summary = "댓글 좋아요 API", description = "commentId: 좋아요 누를 댓글 id, 한번 누르면 좋아요 두번 누르면 좋아요 취소")
    public ApiResponse<?> likeComment(@PathVariable(name = "commentId") Long commentId,
                                      Authentication authentication) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postCommandService.likeComment(member, commentId);
        return ApiResponse.of(SuccessStatus.POST_COMMENT_LIKE_SUCCESS, null);
    }


    @PostMapping("/{postId}/comment")
    @Operation(summary = "피드 댓글 등록 API", description = "parentId: 댓글인 경우 null 대댓글이라면 작성하려는 댓글 id," +
            "postId: 댓글 등록할 피드, content: 댓글 내용 ")
    public ApiResponse<?> uploadComment(@PathVariable(name = "postId") Long postId,
                                        Authentication authentication,
                                        @RequestBody PostRequestDTO.CommentDTO request) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postCommandService.uploadComment(member, postId, request);

        return ApiResponse.of(SuccessStatus.POST_COMMENT_SUCCESS, null);
    }

    @PutMapping("/{postId}/comment/{commentId}")
    @Operation(summary = "피드 댓글 수정 API", description = "postId: 댓글 수정할 피드, commentId : 수정할 댓글, content: 댓글 내용")
    public ApiResponse<?> updateComment(@PathVariable(name = "postId") Long postId,
                                        @PathVariable(name = "commentId") Long commentId,
                                        @RequestBody PostRequestDTO.UpdateCommentDTO requet,
                                        Authentication authentication) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postCommandService.updateComment(member, postId, commentId, requet);

        return ApiResponse.of(SuccessStatus.POST_UPDATE_COMMENT_SUCCESS, null);

    }


}
