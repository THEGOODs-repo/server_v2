package com.umc.TheGoods.repository.post;


import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.post.domain.CommentLike;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByMember_IdAndComment_Id(Long memberId, Long commentId);
}
