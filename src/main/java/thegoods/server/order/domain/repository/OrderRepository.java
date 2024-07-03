package thegoods.server.order.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.order.domain.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}
