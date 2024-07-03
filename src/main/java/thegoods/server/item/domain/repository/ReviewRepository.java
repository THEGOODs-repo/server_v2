package thegoods.server.item.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.item.domain.Review;
import thegoods.server.order.domain.OrderItem;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrderItem(OrderItem orderItem);
}
