package thegoods.server.item.converter;

import thegoods.server.item.domain.ItemImg;

public class ItemImageConverter {
    public static ItemImg toItemImg(String imgUrl, Boolean isThumbnail) {
        return ItemImg.builder()
                .thumbnail(isThumbnail)
                .url(imgUrl)
                .build();
    }
}
