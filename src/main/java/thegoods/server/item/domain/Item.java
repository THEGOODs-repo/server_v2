package thegoods.server.item.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.common.enums.ItemStatus;
import thegoods.server.common.enums.types.DeliveryType;
import thegoods.server.item.presentation.dto.ItemRequestDTO;
import thegoods.server.member.domain.Member;
import thegoods.server.order.domain.OrderItem;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Item extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false, length = 300)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    @ColumnDefault("'ONSALE'")
    private ItemStatus status;

    @Column(length = 6)
    private Integer stock;

    private Long price;

    @Column(nullable = false, length = 6)
    private Integer deliveryFee;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private DeliveryType deliveryType;

    @Column(nullable = false, length = 2)
    private Integer deliveryDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    private Boolean isLimitless;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long dibsCount;

    @Column(nullable = false)
    private Long salesCount;

    @Column
    private Integer tagsCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemImg> itemImgList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemOption> itemOptionList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemTag> itemTagList = new ArrayList<>();

//    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
//    private List<Cart> itemCartList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemView> itemViewList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Dibs> dibsList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Review> reviewList = new ArrayList<>();

    //@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    //private List<Inquiry> inquiryList = new ArrayList<>();

    // 판매수, 재고 관련 메소드
    public Item updateStock(Integer i) {
        this.stock += i;
        return this;
    }

    public Item updateSales(Integer i) {
        this.salesCount += i;
        return this;
    }

    public Item updateViewCounts() {
        this.viewCount += 1;
        return this;
    }

    public Item updateTagCounts(int value) {
        this.tagsCount = value;
        return this;
    }

    public void setCategory(Category category) {
        if (this.category != null)
            category.getItemList().remove(this);
        this.category = category;
        category.getItemList().add(this);
    }

    public void setMember(Member member) {
        if (this.member != null)
            member.getItemList().remove(this);
        this.member = member;
        member.getItemList().add(this);
    }

    public List<ItemOption> getItemOptionList() {
        if (this.itemOptionList == null) {
            this.itemOptionList = new ArrayList<>();
        }
        return this.itemOptionList;
    }

    public List<ItemImg> getItemImgList() {
        if (this.itemImgList == null) {
            this.itemImgList = new ArrayList<>();
        }
        return this.itemImgList;
    }

    public void updateStatus(ItemStatus itemStatus) {
        this.status = itemStatus;
    }

    public void updateItem(ItemRequestDTO.UpdateItemDTO updateItemDTO) {

        DeliveryType deliveryType = null;

        switch (updateItemDTO.getDeliveryType()) {
            case 1:
                deliveryType = DeliveryType.PO;
                break;
            case 2:
                deliveryType = DeliveryType.CJ;
                break;
            case 3:
                deliveryType = DeliveryType.LOTTE;
                break;
            case 4:
                deliveryType = DeliveryType.LOGEN;
                break;
            case 5:
                deliveryType = DeliveryType.HANJIN;
                break;
            case 6:
                deliveryType = DeliveryType.GS25;
                break;
            case 7:
                deliveryType = DeliveryType.CU;
                break;
            case 8:
                deliveryType = DeliveryType.ETC;
                break;
        }

        this.name = updateItemDTO.getName();
        this.description = updateItemDTO.getDescription();
        this.price = updateItemDTO.getPrice();
        this.stock = updateItemDTO.getStock();
        this.deliveryType = deliveryType;
        this.deliveryDate = updateItemDTO.getDeliveryDate();
        this.deliveryFee = updateItemDTO.getDeliveryFee();
        this.isLimitless = updateItemDTO.getIsLimitless();
        this.startDate = updateItemDTO.getStartDate();
        this.endDate = updateItemDTO.getEndDate();
    }
}
