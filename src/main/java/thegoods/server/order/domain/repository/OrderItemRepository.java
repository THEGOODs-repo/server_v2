package thegoods.server.order.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.common.enums.OrderStatus;
import thegoods.server.order.domain.OrderItem;
import thegoods.server.order.domain.Orders;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findAllByStatusAndOrdersIn(OrderStatus orderStatus, List<Orders> ordersList, PageRequest pageRequest);

    Page<OrderItem> findAllByOrdersIn(List<Orders> ordersList, PageRequest pageRequest);

    Page<OrderItem> findAllByOrders(Orders orders, PageRequest pageRequest);

    Optional<OrderItem> findById(Long orderItemId);
}
