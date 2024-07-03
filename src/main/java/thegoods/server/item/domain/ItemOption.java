package thegoods.server.item.domain;

import lombok.*;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.order.domain.OrderDetail;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ItemOption extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_option_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false, length = 6)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @OneToMany(mappedBy = "itemOption", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetailList = new ArrayList<>();


    // 판매수, 재고 관련 메소드
    public ItemOption updateStock(Integer i) {
        this.stock += i;
        return this;
    }

    public void setItem(Item item) {
        if (this.item != null)
            item.getItemOptionList().remove(this);
        this.item = item;
        item.getItemOptionList().add(this);
    }
}

