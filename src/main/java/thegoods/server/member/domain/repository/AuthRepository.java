package thegoods.server.member.domain.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.member.domain.Auth;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByPhone(String phone);

    Optional<Auth> findByEmail(String email);
}
