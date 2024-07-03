package thegoods.server.item.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.item.domain.Category;
import thegoods.server.item.domain.CategoryTag;
import thegoods.server.item.domain.Tag;

import java.util.Optional;

public interface CategoryTagRepository extends JpaRepository<CategoryTag, Long> {

    Optional<CategoryTag> findByCategoryAndTag(Category category, Tag tag);

}
