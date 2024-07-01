package thegoods.server.post.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDTO {
        private Long parentId;
        private String comment;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCommentDTO {
        private String comment;
    }
}
