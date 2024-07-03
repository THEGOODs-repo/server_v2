package thegoods.server.item.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.item.domain.ItemView;

import java.util.List;

public interface ItemViewRepository extends JpaRepository<ItemView, Long> {

    List<ItemView> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);
}
