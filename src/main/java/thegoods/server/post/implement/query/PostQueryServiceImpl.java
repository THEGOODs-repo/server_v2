package thegoods.server.post.implement.query;

import com.umc.TheGoods.domain.community.Post;
import com.umc.TheGoods.repository.post.PostRepository;
import com.umc.TheGoods.web.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;

    @Override
    public List<PostResponseDto> getAllPostsSortedByLikes() {
        List<Post> posts = postRepository.findAllByOrderByLikesCountDesc();
        return posts.stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }
}
