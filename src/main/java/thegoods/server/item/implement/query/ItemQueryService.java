package thegoods.server.item.implement.query;

import org.springframework.data.domain.Page;
import thegoods.server.item.domain.Item;
import thegoods.server.item.domain.ItemOption;
import thegoods.server.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface ItemQueryService {

    Optional<Item> findItemById(Long id);

    Optional<ItemOption> findItemOptionById(Long id);

    boolean isExistItem(Long id);

    boolean isExistItemOption(Long id);

    public Page<Item> getMyItemList(Member member, Integer page);

    Page<Item> getTodayItemList(Integer page);

    public Page<Item> getSimilarItemList(Long itemId, Member member, Integer page);

    Page<Item> getTopSaleItemList(Integer page);

    Page<Item> getSteadySaleItemList(Integer page);

    public Page<Item> getItemByTagCount(Integer page);

    public Page<Item> getItemByDeliveryDate(Integer page);

    public Page<Item> searchItem(Member member, String itemName, String categoryName, String sellerName, List<String> tagName, String type, Integer page);

    Page<Item> getMainItem(String type, Integer page);

    Page<Item> getRelatedItem(Long itemId, Integer page);

}

