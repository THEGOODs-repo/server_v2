package thegoods.server.item.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.item.domain.ItemOption;

import java.util.List;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {

    List<ItemOption> findByItemId(Long itemId);
}
