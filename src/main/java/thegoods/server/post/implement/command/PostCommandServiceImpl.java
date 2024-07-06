package thegoods.server.post.implement.command;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.utils.UtilService;
import thegoods.server.item.domain.repository.FollowRepository;
import thegoods.server.member.domain.Follow;
import thegoods.server.member.domain.Member;
import thegoods.server.member.domain.repository.MemberRepository;
import thegoods.server.member.exception.handler.MemberHandler;
import thegoods.server.post.converter.PostConverter;
import thegoods.server.post.domain.*;
import thegoods.server.post.domain.repository.*;
import thegoods.server.post.exception.handler.PostHandler;
import thegoods.server.post.presentation.dto.PostRequestDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostCommandServiceImpl implements PostCommandService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final UtilService utilService;
    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public void follow(Long followingId, Member follower) {

        if (followingId.equals(follower.getId())) {
            throw new PostHandler(ErrorStatus.POST_SELF_FOLLOW);
        }
        Optional<Follow> check = followRepository.findByFollowingIdAndFollowerId(followingId, follower.getId());
        if (!check.isEmpty()) {
            throw new PostHandler(ErrorStatus.POST_ALREADY_FOLLOW);
        }

        Member following = memberRepository.findById(followingId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Follow follow = PostConverter.toFollow(follower, following);
        followRepository.save(follow);
    }

    @Override
    public void deleteFollow(Long followingId, Long followerId) {

        Follow follow = followRepository.findByFollowingIdAndFollowerId(followingId, followerId).orElseThrow(() -> new PostHandler(ErrorStatus.POST_FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
    }

    @Override
    public void registerPost(Member member, String content, List<MultipartFile> multipartFileList) {
        Post post = PostConverter.toPost(member, content);

        postRepository.save(post);
        if (multipartFileList != null) {
            List<PostImg> postImgList = multipartFileList.stream().map(multipartFile -> {
                String postImgUrl = utilService.uploadS3Img("post", multipartFile);

                PostImg postImg = PostConverter.toPostImg(postImgUrl, post);
                return postImg;
            }).collect(Collectors.toList());

            postImgRepository.saveAll(postImgList);
        }


    }

    @Override
    public void updatePost(Member member, Long postId, String content, List<MultipartFile> multipartFileList) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));
        post.updatePost(content);

        List<PostImg> postImgList = postImgRepository.findByPostId(postId);
        postImgRepository.deleteAll(postImgList);

        if (multipartFileList != null) {
            List<PostImg> newPostImgList = multipartFileList.stream().map(multipartFile -> {
                String postImgUrl = utilService.uploadS3Img("post", multipartFile);

                PostImg postImg = PostConverter.toPostImg(postImgUrl, post);
                return postImg;
            }).collect(Collectors.toList());

            postImgRepository.saveAll(newPostImgList);
        }


    }


    @Override
    public void deletePost(Member member, Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));

        postRepository.delete(post);
    }

    @Override
    public void likePost(Member member, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));
        Optional<PostLike> postLike = postLikeRepository.findByMember_IdAndPost_Id(member.getId(), postId);

        if (postLike.isPresent()) {
            if (post.getLikesCount() > 0) {
                postRepository.updateUnlikeCount(postId);
            }

            postLikeRepository.delete(postLike.get());
        } else {
            postRepository.updateLikeCount(post.getId());

            postLikeRepository.save(PostConverter.toPostLike(member, post));
        }
    }


    @Override
    public void uploadComment(Member member, Long postId, PostRequestDTO.CommentDTO request) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));
        Comment comment = PostConverter.toComment(member, post, request.getComment());

        Comment parentComment;
        if (request.getParentId() != null) {
            parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new PostHandler(ErrorStatus.POST_COMMENT_NOT_FOUND));
            comment.updateParent(parentComment);
        }

        commentRepository.save(comment);
    }

    @Override
    public void updateComment(Member member, Long postId, Long commentId, PostRequestDTO.UpdateCommentDTO request) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new PostHandler(ErrorStatus.POST_COMMENT_NOT_FOUND));
        if (!comment.getMember().equals(member)) {
            throw new PostHandler(ErrorStatus.POST_COMMENT_NOT_UPDATE);
        }

        commentRepository.updateComment(commentId, request.getComment());


    }

    @Override
    public void likeComment(Member member, Long commentId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new PostHandler(ErrorStatus.POST_COMMENT_NOT_FOUND));
        Optional<CommentLike> commentLike = commentLikeRepository.findByMember_IdAndComment_Id(member.getId(), commentId);

        if (commentLike.isPresent()) {
            if (comment.getLikesCount() > 0) {
                commentRepository.updateUnlikeCount(commentId);
            }
            commentLikeRepository.delete(commentLike.get());
        } else {
            commentRepository.updateLikeCount(commentId);
            commentLikeRepository.save(PostConverter.toCommentLike(member, comment));
        }
    }
}
