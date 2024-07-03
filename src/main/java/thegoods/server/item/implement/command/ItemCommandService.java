package thegoods.server.item.implement.command;

import org.springframework.web.multipart.MultipartFile;
import thegoods.server.item.domain.Item;
import thegoods.server.item.presentation.dto.ItemRequestDTO;
import thegoods.server.member.domain.Member;

import java.util.List;

public interface ItemCommandService {
    public Item uploadItem(Member member, ItemRequestDTO.UploadItemDTO request, MultipartFile itemThumbnail, List<MultipartFile> multipartFileList);

    public Item updateItem(Long itemId, Member member, ItemRequestDTO.UpdateItemDTO request, MultipartFile itemThumbnail, List<MultipartFile> multipartFileList);

    public Item getItemContent(Long itemId, Member member);
}
