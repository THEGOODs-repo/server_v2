package thegoods.server.item.domain;

import lombok.*;
import thegoods.server.common.domain.BaseDateTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ItemTag extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public void setItem(Item item) {
        if (this.item != null)
            item.getItemTagList().remove(this);
        this.item = item;
        item.getItemTagList().add(this);
    }

    public void setTag(Tag tag) {
        if (this.tag != null)
            tag.getItemTagList().remove(this);
        this.tag = tag;
        tag.getItemTagList().add(this);
    }
}
