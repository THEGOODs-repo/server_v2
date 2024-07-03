package thegoods.server.item.implement.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thegoods.server.item.domain.repository.TagRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagQueryServiceImpl implements TagQueryService {

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public boolean existTagById(Long id) {
        return tagRepository.existsById(id);
    }
}
