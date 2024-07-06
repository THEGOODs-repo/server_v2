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
public class Address extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Column(columnDefinition = "VARCHAR(80)", nullable = false)
    private String addressName;

    @Column(columnDefinition = "VARCHAR(20)")
    private String addressSpec;

    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    private String deliveryMemo;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String zipcode;

    @Column(columnDefinition = "BOOLEAN")
    @ColumnDefault("false")
    private Boolean defaultCheck;

    @Column(columnDefinition = "VARCHAR(20)")
    private String recipientPhone;

    @Column(columnDefinition = "VARCHAR(10)")
    private String recipientName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


}
