package thegoods.server.order.domain;

import lombok.*;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.common.enums.types.OrderCancelReasonType;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderCancel extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_cancel_id")
    private Long id;

    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private OrderCancelReasonType reasonType;

    @Column(columnDefinition = "TEXT")
    private String reasonDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;
}
