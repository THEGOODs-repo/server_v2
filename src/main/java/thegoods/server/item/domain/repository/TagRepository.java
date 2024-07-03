package thegoods.server.item.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.item.domain.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    Optional<Tag> findFirstByNameOrderByCreatedAtAsc(String name);

    boolean existsByName(String name);
}
