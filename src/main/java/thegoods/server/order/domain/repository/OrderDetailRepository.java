package thegoods.server.order.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.order.domain.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
