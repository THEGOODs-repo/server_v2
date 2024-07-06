package thegoods.server.item.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.member.domain.Follow;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowingIdAndFollowerId(Long followingId, Long followerId);
}
