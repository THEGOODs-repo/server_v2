package thegoods.server.post.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.member.domain.Member;

import javax.persistence.*;

@Entity
@Table(name = "comment_mention")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentMention extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_mention_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "mentioned_nickname", columnDefinition = "VARCHAR(15)", nullable = false)
    private String mentionedNickname;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;
}
