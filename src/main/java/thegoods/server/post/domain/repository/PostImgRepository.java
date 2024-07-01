package com.umc.TheGoods.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.post.domain.PostImg;

import java.util.List;

public interface PostImgRepository extends JpaRepository<PostImg, Long> {
    List<PostImg> findByPostId(Long postId);
}
