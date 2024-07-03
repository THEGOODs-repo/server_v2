package thegoods.server.data.implement;

import org.springframework.web.multipart.MultipartFile;
import thegoods.server.data.dto.DataRequestDTO;
import thegoods.server.member.domain.Member;
import thegoods.server.item.domain.Item;

import java.util.List;

public interface DataCommandService {
    Member addMember(DataRequestDTO.setMemberDTO request);

    Item addItem(Member member, DataRequestDTO.setItemDTO request, MultipartFile itemThumbnail, List<MultipartFile> itemImgList);

}
