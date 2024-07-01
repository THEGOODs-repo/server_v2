package thegoods.server.post.implement.query;

import thegoods.server.post.presentation.dto.PostResponseDTO;

import java.util.List;

public interface PostQueryService {

    List<PostResponseDTO> getAllPostsSortedByLikes();
}
