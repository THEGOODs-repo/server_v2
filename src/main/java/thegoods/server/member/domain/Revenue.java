package thegoods.server.member.domain;



import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import thegoods.server.common.domain.BaseDateTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Revenue extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "revenue_id")
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer withdrawPossible;//출금 가능

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer withdrawCompleted; //출금 완료

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer withdrawPredicted; //출금 예상

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
