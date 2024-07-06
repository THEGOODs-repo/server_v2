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
public class Account extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String owner;

    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String bankName;


    @Column(columnDefinition = "VARCHAR(16)", nullable = false)
    private String accountNum;

    @Column(columnDefinition = "BOOLEAN")
    @ColumnDefault("false")
    private Boolean defaultCheck;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
