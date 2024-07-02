package thegoods.server.common.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import thegoods.server.common.config.AmazonConfig;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;


    public String uploadFile(String path, Uuid uuid, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        String keyName = "";
        switch (path) {
            case "member":
                keyName = generateMemberKeyName(uuid);
                break;

            case "item":
                keyName = generateItemKeyName(uuid);
                break;

            case "review":
                keyName = generateReviewKeyName(uuid);
                break;

            case "post":
                keyName = generatePostKeyName(uuid);
                break;
            default:
                keyName = "./" + uuid.getUuid();
        }

        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String getUrl(String keyName) {
        keyName = amazonConfig.getMemberPath() + '/' + keyName;
        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String generateMemberKeyName(Uuid uuid) {
        return amazonConfig.getMemberPath() + '/' + uuid.getUuid();
    }

    public String generateItemKeyName(Uuid uuid) {
        return amazonConfig.getItemPath() + '/' + uuid.getUuid();
    }

    public String generateReviewKeyName(Uuid uuid) {
        return amazonConfig.getReviewPath() + '/' + uuid.getUuid();
    }

    public String generatePostKeyName(Uuid uuid) {
        return amazonConfig.getPostPath() + '/' + uuid.getUuid();
    }

}
