package thegoods.server.post.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.post.domain.PostLike;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByMember_IdAndPost_Id(Long memberId, Long postId);
}
