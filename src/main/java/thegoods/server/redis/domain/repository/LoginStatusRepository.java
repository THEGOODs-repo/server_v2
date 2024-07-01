package thegoods.server.redis.domain.repository;

import org.springframework.data.repository.CrudRepository;
import thegoods.server.redis.domain.LoginStatus;

public interface LoginStatusRepository extends CrudRepository<LoginStatus, String> {
}
