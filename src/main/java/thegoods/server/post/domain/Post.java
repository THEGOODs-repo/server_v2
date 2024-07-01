package thegoods.server.post.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.member.domain.Member;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@Builder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 생성 로직 규정
public class Post extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> postLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostImg> postImgList = new ArrayList<>();

    @ColumnDefault("0")
    @Column(nullable = false)
    private Long viewCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer likesCount;

    public void updatePost(String content) {
        this.content = content;
    }
}
