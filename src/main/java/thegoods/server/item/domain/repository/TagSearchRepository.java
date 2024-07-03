package thegoods.server.item.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.item.domain.TagSearch;

public interface TagSearchRepository extends JpaRepository<TagSearch, Long> {
}
