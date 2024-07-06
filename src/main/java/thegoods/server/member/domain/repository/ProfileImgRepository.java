package thegoods.server.member.domain.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.member.domain.ProfileImg;

import java.util.Optional;

public interface ProfileImgRepository extends JpaRepository<ProfileImg, Long> {

    Optional<ProfileImg> findByMember_Id(Long id);

    void deleteByMember_Id(Long id);
}
