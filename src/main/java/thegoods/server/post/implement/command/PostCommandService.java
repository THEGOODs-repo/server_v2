package thegoods.server.post.implement.command;

import org.springframework.web.multipart.MultipartFile;
import thegoods.server.member.domain.Member;
import thegoods.server.post.presentation.dto.PostRequestDTO;

import java.util.List;

public interface PostCommandService {

    void follow(Long followingId, Member follower);

    void deleteFollow(Long followingId, Long followerId);

    void registerPost(Member member, String content, List<MultipartFile> multipartFileList);

    void updatePost(Member member, Long postId, String content, List<MultipartFile> multipartFileList);

    void deletePost(Member member, Long postId);

    void likePost(Member member, Long postId);


    void uploadComment(Member member, Long postId, PostRequestDTO.CommentDTO request);

    void updateComment(Member member, Long postId, Long commentId, PostRequestDTO.UpdateCommentDTO request);

    void likeComment(Member member, Long commentId);
}
