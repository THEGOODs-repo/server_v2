package thegoods.server.item.domain;

import lombok.*;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.member.domain.Member;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ItemView extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_view_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public void setMember(Member member) {
        if (this.member != null)
            member.getItemViewList().remove(this);
        this.member = member;
        member.getItemViewList().add(this);
    }

    public void setItem(Item item) {
        if (this.item != null)
            item.getItemViewList().remove(this);
        this.item = item;
        item.getItemViewList().add(this);
    }
}

