package thegoods.server.post.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "post_img")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_img_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    public void setPost(Post post) {
        if (this.post != null)
            post.getPostImgList().remove(this);
        this.post = post;
        post.getPostImgList().add(this);
    }
}
