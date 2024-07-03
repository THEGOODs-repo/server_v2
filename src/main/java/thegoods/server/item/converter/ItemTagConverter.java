package thegoods.server.item.converter;

import thegoods.server.item.domain.ItemTag;
import thegoods.server.item.domain.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class ItemTagConverter {

    public static List<ItemTag> toItemTagList(List<Tag> tagList) {

        return tagList.stream()
                .map(tag ->
                        ItemTag.builder()
                                .tag(tag)
                                .build()
                ).collect(Collectors.toList());
    }
}
