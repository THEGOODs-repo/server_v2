package thegoods.server.order.implement.query;

import org.springframework.data.domain.Page;
import thegoods.server.common.enums.OrderStatus;
import thegoods.server.member.domain.Member;
import thegoods.server.member.presentation.dto.MemberResponseDTO;
import thegoods.server.order.domain.OrderItem;
import thegoods.server.order.presentation.dto.OrderRequestDTO;

public interface OrderQueryService {
    Page<OrderItem> getOrderItemList(Member member, OrderStatus orderStatus, Integer page);

    Page<OrderItem> getNoLoginOrderItemList(OrderRequestDTO.noLoginOrderViewDTO request);

    /**
     * orderItemId에 해당하는 orderItem을 조회. member를 통해 해당 orderItem을 조회할 권한이 있는지 검증
     *
     * @param member
     * @param orderItemId
     * @return
     */
    OrderItem getOrderItem(Member member, Long orderItemId);

    boolean isExistOrders(Long id);

    boolean isExistOrderItem(Long id);

    MemberResponseDTO.MyPageOrderItemListDTO getMyPageOrderItemList(Member member, OrderStatus orderStatus, Integer pageIdx);

}
