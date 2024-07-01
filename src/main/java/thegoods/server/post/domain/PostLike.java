package thegoods.server.post.domain;

import lombok.*;
import thegoods.server.common.domain.BaseDateTimeEntity;
import thegoods.server.member.domain.Member;

import javax.persistence.*;

@Entity
@Table(name = "post_like")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 생성 로직 규정
public class PostLike extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
