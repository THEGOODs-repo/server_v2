package thegoods.server.item.converter;

import org.springframework.data.domain.Page;
import thegoods.server.common.enums.ItemStatus;
import thegoods.server.common.enums.types.DeliveryType;
import thegoods.server.item.domain.Item;
import thegoods.server.item.domain.ItemImg;
import thegoods.server.item.domain.ItemOption;
import thegoods.server.item.presentation.dto.ItemRequestDTO;
import thegoods.server.item.presentation.dto.ItemResponseDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemConverter {
    public static ItemResponseDTO.ItemImgResponseDTO getItemImgDTO(ItemImg itemImg) {
        return ItemResponseDTO.ItemImgResponseDTO.builder()
                .isThumbNail(itemImg.getThumbnail())
                .itemImgUrl(itemImg.getUrl())
                .build();
    }

    public static ItemResponseDTO.ItemOptionResponseDTO getItemOptionDTO(ItemOption itemOption) {
        return ItemResponseDTO.ItemOptionResponseDTO.builder()
                .itemOptionId(itemOption.getId())
                .name(itemOption.getName())
                .stock(itemOption.getStock())
                .price(itemOption.getPrice())
                .build();
    }

    public static ItemResponseDTO.ItemContentDTO getItemContentDTO(Item item) {
        List<ItemResponseDTO.ItemImgResponseDTO> itemImgResponseDTOList = item.getItemImgList().stream()
                .map(ItemConverter::getItemImgDTO).collect(Collectors.toList());

        List<ItemResponseDTO.ItemOptionResponseDTO> itemOptionResponseDTOList = item.getItemOptionList().stream()
                .map(ItemConverter::getItemOptionDTO).collect(Collectors.toList());

        return ItemResponseDTO.ItemContentDTO.builder()
                .itemId(item.getId())
                .name(item.getName())
                .memberName(item.getMember().getNickname())
                .memberProfileImgUrl(item.getMember().getProfileImg().getUrl())
                .category(item.getCategory().getName())
                .deliveryDate(item.getDeliveryDate())
                .deliveryFee(item.getDeliveryFee())
                .deliveryType(item.getDeliveryType())
                .status(item.getStatus())
                .isLimitless(item.getIsLimitless())
                .stock(item.getStock())
                .price(item.getPrice())
                .description(item.getDescription())
                .startDate(item.getStartDate())
                .endDate(item.getEndDate())
                .salesCount(item.getSalesCount())
                .viewCount(item.getViewCount())
                .dibsCount(item.getDibsCount())
                .itemTag(item.getItemTagList().stream().map(tag -> tag.getTag().getName()).toList())
                .itemImgUrlList(itemImgResponseDTOList)
                .itemOptionList(itemOptionResponseDTOList)
                .build();
    }

    public static ItemResponseDTO.ItemPreviewDTO itemPreviewDTO(Item item) {
//        List<ItemResponseDTO.ItemImgResponseDTO> itemImgResponseDTOList = item.getItemImgList().stream()
//                .map(ItemConverter::getItemImgDTO).collect(Collectors.toList());

        List<ItemResponseDTO.ItemImgResponseDTO> itemImgResponseDTOList = item.getItemImgList().stream()
                .map(ItemConverter::getItemImgDTO)
                .filter(ItemResponseDTO.ItemImgResponseDTO::getIsThumbNail).collect(Collectors.toList());


        return ItemResponseDTO.ItemPreviewDTO.builder()
                .itemId(item.getId())
                .name(item.getName())
                .status(item.getStatus())
                .viewCount(item.getViewCount())
                .endDate(item.getEndDate())
                .memberName(item.getMember().getNickname())
                .memberProfileImgUrl(item.getMember().getProfileImg().getUrl())
                .itemImgUrlList(itemImgResponseDTOList)
                .createdAt(item.getCreatedAt())
                .build();
    }

    public static ItemResponseDTO.ItemPreviewListDTO itemPreviewListDTO(Page<Item> itemList) {

        List<ItemResponseDTO.ItemPreviewDTO> itemPreviewDTOList = itemList.stream()
                .map(ItemConverter::itemPreviewDTO).collect(Collectors.toList());

        return ItemResponseDTO.ItemPreviewListDTO.builder()
                .itemList(itemPreviewDTOList)
                .isFirst(itemList.isFirst())
                .isLast(itemList.isLast())
                .listSize(itemList.getSize())
                .totalPage(itemList.getTotalPages())
                .totalElements(itemList.getTotalElements())
                .build();
    }

    public static ItemResponseDTO.UploadItemResultDTO toUploadItemResultDTO(Item item) {
        return ItemResponseDTO.UploadItemResultDTO.builder()
                .itemId(item.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ItemResponseDTO.UpdateItemResultDTO toUpdateItemResultDTO(Item item) {
        return ItemResponseDTO.UpdateItemResultDTO.builder()
                .itemId(item.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }


    public static Item toItem(ItemRequestDTO.UploadItemDTO request) {

        DeliveryType deliveryType = null;

        switch (request.getDeliveryType()) {
            case 1:
                deliveryType = DeliveryType.PO;
                break;
            case 2:
                deliveryType = DeliveryType.CJ;
                break;
            case 3:
                deliveryType = DeliveryType.LOTTE;
                break;
            case 4:
                deliveryType = DeliveryType.LOGEN;
                break;
            case 5:
                deliveryType = DeliveryType.HANJIN;
                break;
            case 6:
                deliveryType = DeliveryType.GS25;
                break;
            case 7:
                deliveryType = DeliveryType.CU;
                break;
            case 8:
                deliveryType = DeliveryType.ETC;
                break;
        }

        return Item.builder()
                .name(request.getName())
                .deliveryType(deliveryType)
                .description(request.getDescription())
                .deliveryFee(request.getDeliveryFee())
                .deliveryDate(request.getDeliveryDate())
                .isLimitless(request.getIsLimitless())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .dibsCount(0L)
                .viewCount(0L)
                .salesCount(0L)
                .tagsCount(0)
                .status(ItemStatus.ONSALE)
                .price(request.getPrice())
                .stock(request.getStock())
                .itemTagList(new ArrayList<>())
                .itemImgList(new ArrayList<>())
                .build();
    }
}
