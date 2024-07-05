package thegoods.server.member.implement.query;

import thegoods.server.member.domain.*;

import java.util.List;
import java.util.Optional;

public interface MemberQueryService {
    Optional<Member> findMemberById(Long id);

    Optional<Member> findMemberByNickname(String name);

    Optional<ProfileImg> findProfileImgByMember(Long id);

    Optional<Member> findMemberByEmail(String email);

    List<Address> findAllAddressById(Long id);

    List<Account> findAllAccountById(Long id);

    List<Account> findAccountById(Long id);


}
