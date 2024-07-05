package thegoods.server.member.domain;

import lombok.*;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.common.enums.types.AlarmType;

import javax.persistence.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmType dtype;

    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

}
