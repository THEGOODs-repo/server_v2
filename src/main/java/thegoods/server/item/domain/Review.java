package thegoods.server.item.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.common.enums.ReviewStatus;
import thegoods.server.member.domain.Member;
import thegoods.server.order.domain.OrderItem;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false, length = 1500)
    private String text;

    @Column(nullable = false, length = 1)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    @ColumnDefault("'SHOW'")
    private ReviewStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;
//
//    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
//    private ReviewImg reviewImg;

    // 연관관계 메소드
    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getReviewList().remove(this);
        }
        this.member = member;
        this.member.getReviewList().add(this);
    }

    public void setItem(Item item) {
        if (this.item != null) {
            this.item.getReviewList().remove(this);
        }
        this.item = item;
        this.item.getReviewList().add(this);
    }

}

