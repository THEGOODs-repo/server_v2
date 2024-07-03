package thegoods.server.item.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import thegoods.server.item.domain.ItemTag;
import thegoods.server.item.presentation.dto.ConsultResultDTO;

import java.util.List;

public interface ItemTagRepository extends JpaRepository<ItemTag, Long> {

    List<ItemTag> findByItemId(Long itemId);

    ItemTag findByTagId(Long tagId);

    @Query(value = "SELECT NEW com.umc.TheGoods.web.dto.item.ConsultResultDTO(it.item.id,COUNT(it.tag.id)) " +
            "FROM ItemTag AS it " +
            "GROUP BY it.item.id " +
            "ORDER BY COUNT(it.tag.id) DESC ")
    List<ConsultResultDTO> countTagsByTagId();
}
