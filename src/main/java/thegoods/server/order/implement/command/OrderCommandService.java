package thegoods.server.order.implement.command;

import thegoods.server.member.domain.Member;
import thegoods.server.order.domain.OrderItem;
import thegoods.server.order.domain.Orders;
import thegoods.server.order.presentation.dto.OrderRequestDTO;

public interface OrderCommandService {

    Orders create(OrderRequestDTO.OrderAddDTO request, Member member);

    OrderItem updateStatusToConfirm(Long orderItemId, Member member);

    OrderItem updateOrderItemAddress(OrderRequestDTO.OrderItemAddressUpdateDTO request, Long orderItemId, Member member);

    OrderItem updateOrderItemRefundInfo(OrderRequestDTO.OrderItemRefundInfoUpdateDTO request, Long orderItemId, Member member);

}
