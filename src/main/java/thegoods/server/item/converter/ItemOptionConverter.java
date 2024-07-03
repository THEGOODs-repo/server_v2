package thegoods.server.item.converter;

import thegoods.server.item.domain.ItemOption;
import thegoods.server.item.presentation.dto.ItemRequestDTO;

public class ItemOptionConverter {

    public static ItemOption toItemOption(ItemRequestDTO.itemOptionDTO request) {
        return ItemOption.builder()
                .name(request.getName())
                .stock(request.getStock())
                .price(request.getPrice())
                .build();
    }
}
