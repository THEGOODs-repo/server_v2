package thegoods.server.redis.domain.repository;

import org.springframework.data.repository.CrudRepository;
import thegoods.server.redis.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {


}
