package thegoods.server.order.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import thegoods.server.common.enums.OrderStatus;
import thegoods.server.common.enums.types.DeliveryType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderAddResultDTO {
        Long orderId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class orderItemPreViewListDTO {
        List<OrderItemPreViewDTO> orderItemPreViewDTOList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemPreViewDTO {
        Long orderItemId;
        LocalDateTime orderDateTime;
        String imgUrl;
        OrderStatus orderStatus;
        String itemName;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String optionString;
        Long price;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemViewDTO {
        Long ordersId; // 주문 번호
        Long orderItemId; // 주문 상품 번호
        String imgUrl;
        String itemName;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String optionString;
        OrderStatus orderStatus;

        BuyerInfoDTO buyerInfoDTO; // 주문자 정보
        OrderItemInfoDTO orderItemInfoDTO; // 주문 정보
        DepositInfoDTO depositInfoDTO; // 나의 입금 정보
        AddressInfoDTO addressInfoDTO; // 주소 정보
        DeliveryInfoDTO deliveryInfoDTO; // 운송장 정보
        RefundInfoDTO refundInfoDTO; // 환불 계좌 정보
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyerInfoDTO {
        String name;
        String phone;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String email; // 회원 주문 조회인 경우에만 포함
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemInfoDTO {
        LocalDateTime orderDateTime;
        Long itemPrice;
        Integer deliveryFee;
        Long totalPrice;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String deliveryNum;
        List<OrderDetailInfoDTO> orderDetailInfoDTOList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailInfoDTO {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String optionName;
        Long price;
        Integer amount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepositInfoDTO {
        String depositor;
        Long depositAmount;
        LocalDate depositDate;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressInfoDTO {
        DeliveryType deliveryType;
        String name;
        String phone;
        String zipcode;
        String address;
        String deliveryMemo;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryInfoDTO {
        DeliveryType deliveryComp;
        String deliveryNum;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundInfoDTO {
        String refundOwner;
        String refundBank;
        String refundAccount;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemUpdateResultDTO {
        Long orderItemId;
        LocalDateTime updatedAt;
    }
}
