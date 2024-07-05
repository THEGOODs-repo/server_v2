package thegoods.server.order.domain;

import lombok.*;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.common.enums.types.PayType;
import thegoods.server.member.domain.Member;
import thegoods.server.payment.domain.Payment;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Orders extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Payment payment;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private PayType payType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList = new ArrayList<>();

    // 연관관계 메소드
    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getOrdersList().remove(this);
        }
        this.member = member;
        member.getOrdersList().add(this);
    }
}

