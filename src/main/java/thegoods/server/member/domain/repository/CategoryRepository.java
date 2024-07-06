package thegoods.server.member.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.item.domain.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Optional<Category> findById(Long id);


}
