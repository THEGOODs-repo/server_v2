package thegoods.server.payment.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.member.domain.Member;
import thegoods.server.order.domain.OrderItem;
import thegoods.server.order.domain.Orders;
import thegoods.server.payment.presentation.dto.PaymentRequestDTO;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private String paymentUid;  // 결제 고유 번호 (imp_uid)

    @Column(nullable = false)
    private String paymentMethod; // 결제 수단

    @Column(nullable = false)
    private String pgProvider; // PG사 정보

    @Column(nullable = false)
    private LocalDateTime paymentTime;

    /**
     * 조회 메서드
     */
    public Long getTotalPrice() {
        Long totalPrice = 0L;

        // 주문 내역 확인
        Orders order = this.orders;
        if (order != null) {
            // 주문 상품 리스트 가져오기
            List<OrderItem> orderItems = order.getOrderItemList();
            for (OrderItem orderItem : orderItems) {
                totalPrice += orderItem.getTotalPrice(); // 주문 상품 가격 합산
            }
        }

        return totalPrice;
    }

    /**
     * 사전 결제 정보 검증 메서드
     *
     * @param paymentRequest 결제 요청 정보
     * @return 검증 결과
     */
    public boolean validatePaymentRequest(PaymentRequestDTO paymentRequest) {
        // 결제 요청 정보가 유효한지 확인하고, 필요한 경우 추가적인 검증 로직을 수행합니다.
        // 예를 들어, 결제 금액, 주문 번호 등의 필수 정보가 올바르게 설정되었는지 확인할 수 있습니다.
        // 이 메서드는 결제 요청이 유효하면 true를 반환하고, 그렇지 않으면 false를 반환합니다.

        // 예시: 결제 금액이 주문 총액과 일치하는지 확인
        //return paymentRequest.getAmount().equals(getTotalPrice());
        return true;
    }

}
