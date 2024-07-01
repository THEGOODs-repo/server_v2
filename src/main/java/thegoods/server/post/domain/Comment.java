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
@Table(name = "comment")
@Getter
@Builder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 생성 로직 규정
public class Comment extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer likesCount;

    /**
     * 대댓글 관련
     * parentComment: 부모 댓글을 참조하는 필드
     * childComments: 현재 댓글에 대한 대댓글들의 목록을 나타내는 필드
     */
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentLike> commentLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentMention> commentMentionList = new ArrayList<>();

    // 엔티티 핵심 메서드
    public void setContent(String newContent) {
        this.content = newContent;
    }

    public void removeCommentLike(Long id) {
        this.commentLikeList.removeIf((like) -> like.getId() == id);
    }

    public void updateParent(Comment comment) {
        this.parentComment = comment;
    }
}
