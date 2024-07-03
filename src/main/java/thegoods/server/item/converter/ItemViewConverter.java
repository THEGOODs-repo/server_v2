package thegoods.server.item.converter;

import thegoods.server.item.domain.Item;
import thegoods.server.item.domain.ItemView;
import thegoods.server.member.domain.Member;

public class ItemViewConverter {

    public static ItemView toItemView(Member member, Item item) {
        return ItemView.builder()
                .item(item)
                .member(member)
                .build();
    }
}
