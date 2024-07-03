package thegoods.server.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilService {

    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    public String uploadS3Img(String path, MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString();
        Uuid saveUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
        String imgUrl = s3Manager.uploadFile(path, saveUuid, multipartFile);

        return imgUrl;
    }
}
