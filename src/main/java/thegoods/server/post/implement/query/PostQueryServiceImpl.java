package thegoods.server.post.implement.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thegoods.server.post.domain.repository.PostRepository;
import thegoods.server.post.presentation.dto.PostResponseDTO;
import thegoods.server.post.domain.Post;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;

    @Override
    public List<PostResponseDTO> getAllPostsSortedByLikes() {
        List<Post> posts = postRepository.findAllByOrderByLikesCountDesc();
        return posts.stream()
                .map(PostResponseDTO::new)
                .collect(Collectors.toList());
    }
}
