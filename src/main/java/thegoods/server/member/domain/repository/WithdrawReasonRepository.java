package thegoods.server.member.domain.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.member.domain.WithdrawReason;

import java.util.Optional;

public interface WithdrawReasonRepository extends JpaRepository<WithdrawReason, Long> {
    Optional<WithdrawReason> findByMember_Id(Long id);
}
