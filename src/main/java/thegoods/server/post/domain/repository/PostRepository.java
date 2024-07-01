package com.umc.TheGoods.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import thegoods.server.post.domain.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.likesCount = p.likesCount +1 WHERE p.id= :postId")
    void updateLikeCount(Long postId);

    @Modifying(clearAutomatically = true)
    @Query("update Post p SET p.likesCount = p.likesCount -1 WHERE p.id= :postId")
    void updateUnlikeCount(Long postId);

    @Modifying(clearAutomatically = true)
    @Query("SELECT p FROM Post p ORDER BY p.likesCount DESC")
    List<Post> findAllByOrderByLikesCountDesc();
}
