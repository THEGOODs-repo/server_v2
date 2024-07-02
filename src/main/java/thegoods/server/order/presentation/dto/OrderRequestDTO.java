package thegoods.server.order.presentation.dto;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

public class OrderRequestDTO {

    @Getter
    public static class OrderAddDTO {

        @NotBlank
        String name;

        @NotBlank
        String phone;

        @NotBlank
        String receiverName;

        @NotBlank
        String receiverPhone;

        @Size(min = 5, max = 6)
        String zipcode;

        @NotBlank
        String address;

        String addressDetail;

        String deliveryMemo;

        @NotBlank
        String payType;

        @NotBlank
        String refundBank;

        @NotBlank
        String refundAccount;

        @NotBlank
        String refundOwner;

        String depositor;

        @NotEmpty
        @Valid
        List<OrderItemDTO> orderItemDTOList;
    }

    @Getter
    @OrderAvailable
    public static class OrderItemDTO {
        @NotNull
        Long itemId;

        @NotEmpty
        List<OrderDetailDTO> orderDetailDTOList;
    }

    @Getter
    public static class OrderDetailDTO {
        Long cartId; // 장바구니에서 주문 요청하는 경우, cartId 값 필요

        Long itemOptionId;

        @Min(1)
        @Max(100000)
        Integer amount;
    }

    @Getter
    public static class noLoginOrderViewDTO {
        @NotNull
        @ExistOrders
        Long ordersId;
        @NotBlank
        String name;
        @NotBlank
        String phone;
        @Min(1)
        Integer page;
    }

    @Getter
    public static class OrderItemAddressUpdateDTO {
        @NotBlank
        String receiverName;
        @NotBlank
        String receiverPhone;
        @Size(min = 5, max = 6)
        String zipcode;
        @NotBlank
        String address;
        String addressDetail;
        String deliveryMemo;
    }

    @Getter
    public static class OrderItemRefundInfoUpdateDTO {
        @NotBlank
        String refundOwner;
        @NotBlank
        String refundBank;
        @NotBlank
        String refundAccount;
    }
}
