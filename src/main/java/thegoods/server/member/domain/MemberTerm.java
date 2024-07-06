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
public class MemberTerm extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_term_id")
    private Long id;

    //알람 동의 여부
    @Column(nullable = false, columnDefinition = "BOOLEAN")
    @ColumnDefault("false")
    private Boolean memberAgree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    public void setMember(Member member) {
        if (this.member != null)
            member.getMemberTermList().remove(this);
        this.member = member;
        member.getMemberTermList().add(this);
    }
}
