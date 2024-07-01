package thegoods.server.member.converter;

import thegoods.server.member.domain.Member;

public class MemberConverter {

    // 기존 레포지토리의 PostConverter에 있는 Follow는 Member 도메인으로 이동 시켰습니다.(추후에 생각해보시고 수정하셔도 됩니다!)
    public static Follow toFollow(Member follower, Member following) {

        return Follow.builder()
                .follower(follower)
                .following(following)
                .build();
    }
}
