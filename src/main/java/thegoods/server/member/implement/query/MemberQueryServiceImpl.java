package thegoods.server.member.implement.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thegoods.server.member.domain.*;
import thegoods.server.member.domain.repository.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService{

    private final MemberRepository memberRepository;
    private final ProfileImgRepository profileImgRepository;

    private final AddressRepository addressRepository;

    private final AccountRepository accountRepository;


    @Override
    public Optional<Member> findMemberById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public Optional<Member> findMemberByNickname(String name) {
        return memberRepository.findByNickname(name);
    }

    @Override
    public Optional<ProfileImg> findProfileImgByMember(Long id) {

        Optional<ProfileImg> profileImg = profileImgRepository.findByMember_Id(id);
        return profileImg;
    }

    @Override
    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public List<Address> findAllAddressById(Long id) {

        List<Address> address = addressRepository.findAllByMember_Id(id);
        return address;
    }

    @Override
    public List<Account> findAllAccountById(Long id) {

        List<Account> account = accountRepository.findAllByMember_Id(id);
        return account;
    }

    public List<Account> findAccountById(Long id){
        return accountRepository.findAllByMember_Id(id);
    }



}
