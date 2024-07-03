package thegoods.server.data;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import thegoods.server.common.enums.Gender;
import thegoods.server.common.enums.ItemStatus;
import thegoods.server.common.enums.MemberRole;
import thegoods.server.common.enums.types.DeliveryType;
import thegoods.server.data.dto.DataRequestDTO;
import thegoods.server.data.dto.DataResponseDTO;
import thegoods.server.item.domain.*;
import thegoods.server.member.domain.Member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DataConverter {
    public static Member toTestMember(DataRequestDTO.setMemberDTO request, BCryptPasswordEncoder encoder) {
        Date date = new Date();

        return Member.builder()
                .nickname(request.getNickname())
                .password(encoder.encode("12345678"))
                .email(request.getNickname() + "@gmail.com")
                .birthday(date)
                .gender(Gender.FEMALE)
                .phone("01012345678")
                .memberRole(MemberRole.SELLER)
                .memberTermList(new ArrayList<>())
                .itemList(new ArrayList<>())
                .build();

    }

    public static ProfileImg toProfileImg(String imgUrl) {
        return ProfileImg.builder()
                .url(imgUrl)
                .build();
    }

    public static Item toTestItem(DataRequestDTO.setItemDTO request) {

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
                .dibsCount(request.getDibsCount())
                .viewCount(request.getViewCount())
                .salesCount(request.getSalesCount())
                .tagsCount(0)
                .status(ItemStatus.ONSALE)
                .price(request.getPrice())
                .stock(request.getStock())
                .itemTagList(new ArrayList<>())
                .itemImgList(new ArrayList<>())
                .build();
    }

    public static ItemImg toItemImg(String imgUrl, boolean isThumbnail) {
        return ItemImg.builder()
                .thumbnail(isThumbnail)
                .url(imgUrl)
                .build();
    }

    public static Tag toTag(String name) {
        return Tag.builder()
                .name(name)
                .categoryTagList(new ArrayList<>())
                .tagSearchList(new ArrayList<>())
                .itemTagList(new ArrayList<>())
                .build();
    }

    public static List<CategoryTag> toCategoryTagList(List<Tag> tagList) {
        return tagList.stream().map(tag -> {
            return CategoryTag.builder().build();
        }).collect(Collectors.toList());
    }

    public static CategoryTag toCategoryTag() {
        return CategoryTag.builder().build();
    }

    public static DataResponseDTO.addItemDTO toAddItemDTO(Item item) {
        return DataResponseDTO.addItemDTO.builder()
                .itemId(item.getId())
                .createdAt(item.getCreatedAt())
                .build();
    }

    public static ItemTag toItemTag() {
        return ItemTag.builder().build();
    }

}
