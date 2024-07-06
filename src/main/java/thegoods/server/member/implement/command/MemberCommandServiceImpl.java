package thegoods.server.member.implement.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import thegoods.server.common.config.MailConfig;
import thegoods.server.common.enums.ItemStatus;
import thegoods.server.common.enums.MemberStatus;
import thegoods.server.common.exception.status.ErrorStatus;

import thegoods.server.common.presentation.ApiResponse;
import thegoods.server.common.security.provider.TokenProvider;
import thegoods.server.common.utils.UtilService;
import thegoods.server.item.domain.Item;
import thegoods.server.item.domain.repository.ItemRepository;
import thegoods.server.member.converter.MemberConverter;
import thegoods.server.member.domain.*;
import thegoods.server.member.domain.repository.*;
import thegoods.server.member.exception.handler.MemberHandler;
import thegoods.server.member.presentation.dto.*;
import thegoods.server.redis.domain.RefreshToken;
import thegoods.server.redis.service.RedisService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final BCryptPasswordEncoder encoder; // 비밀번호 암호화
    private final TermRepository termRepository;
    private final AuthRepository authRepository;
    private final MailConfig mailConfig;
    private final ProfileImgRepository profileImgRepository;
    private final UtilService utilService;
    private final AddressRepository addressRepository;
    private final AccountRepository accountRepository;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;
    private final WithdrawReasonRepository withdrawReasonRepository;
    private final ItemRepository itemRepository;

    @Value("${jwt.token.secret}")
    private String key; // 토큰 만들어내는 key값
    private int expiredMs = 1000 * 60 * 60 * 24 * 5;// 토큰 만료 시간 1일

    @Value("${social.kakao.client-id}")
    private String kakao_client_id;

    @Value("${social.kakao.redirect-uri}")
    private String kakao_redirect_uri;

    @Value("${social.naver.client-id}")
    private String naver_client_id;

    @Value("${social.naver.redirect-uri}")
    private String naver_redirect_uri;

    @Value("${social.naver.client-secret}")
    private String naver_client_secret;


    /**
     * 회원가입 api
     *
     * @param
     * @return
     */
    @Transactional
    @Override
    public Member join(MemberRequestDTO.JoinDTO request) {

        Member existingMember = null;


        Optional<Member> existingNickname = memberRepository.findByNickname(request.getNickname());
        if (existingNickname.isPresent()) {
            if (!existingNickname.get().getMemberStatus().equals(MemberStatus.INACTIVE)) {
                throw new MemberHandler(ErrorStatus.MEMBER_NICKNAME_DUPLICATED);
            } else {
                //탈퇴한 회원인 경우
                existingMember = existingNickname.get();
            }
        }

        Optional<Member> existingEmail = memberRepository.findByEmail(request.getEmail());
        if (existingEmail.isPresent()) {
            if (!existingEmail.get().getMemberStatus().equals(MemberStatus.INACTIVE)) {
                throw new MemberHandler(ErrorStatus.MEMBER_EMAIL_DUPLICATED);
            } else {
                //탈퇴한 회원인 경우
                existingMember = existingEmail.get();
            }
        }

        Member member;
        if (existingMember != null) {
            //탈퇴한 회원이 다시 회원가입하는 경우
            existingMember.setMemberStatus(MemberStatus.ACTIVE);
            memberRepository.reregister(existingMember.getId(),request.getNickname(),
                    request.getName(), encoder.encode(request.getPassword()), request.getEmail(),
                    request.getBirthday(), request.getGender(), request.getPhone());
            return existingMember;
        } else {
            member = MemberConverter.toMember(request, encoder);
        }

        // 약관동의 저장 로직
        HashMap<Term, Boolean> termMap = new HashMap<>();
        for (int i = 0; i < request.getMemberTerm().size(); i++) {
            termMap.put(termRepository.findById(i + 1L).orElseThrow(() -> new MemberHandler(ErrorStatus.TERM_NOT_FOUND)), request.getMemberTerm().get(i));

        }

        List<MemberTerm> memberTermList = MemberConverter.toMemberTermList(termMap);
        memberTermList.forEach(memberTerm -> {
            memberTerm.setMember(member);
        });
        memberRepository.save(member);
        return member;
    }

    /**
     * 로그인 api
     *
     * @return
     */
    @Override
    public MemberResponseDTO.LoginResultDTO login(MemberRequestDTO.LoginDTO request, HttpServletResponse response) {

        //email 없음
        Member selectedMember = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_EMAIL_NOT_FOUND));

        if(!this.checkDeregister(selectedMember)){
            throw new MemberHandler(ErrorStatus.MEMBER_INACTIVATE);
        }
        //password 틀림
        if (!encoder.matches(request.getPassword(), selectedMember.getPassword())) {
            throw new MemberHandler(ErrorStatus.MEMBER_PASSWORD_ERROR);
        }

        // refresh 생성후 쿠키로 만들고 response 헤더에 담기
        RefreshToken refreshToken = redisService.generateRefreshToken(request.getEmail());
        Cookie cookie = new Cookie("refreshToken",refreshToken.getToken());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime accessExpireTime = currentDateTime.plusHours(6);



        return MemberResponseDTO.LoginResultDTO.builder()
                .accessToken(redisService.saveLoginStatus(selectedMember.getId(), tokenProvider.createAccessToken(selectedMember.getId(), selectedMember.getMemberRole().toString() , request.getEmail(), Arrays.asList(new SimpleGrantedAuthority("USER")))))
                .accessExpireTime(accessExpireTime)
                .build();

    }

    @Override
    public String regenerateAccessToken(RefreshToken refreshToken) {
        Member member = memberRepository.findById(refreshToken.getMemberId()).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return redisService.saveLoginStatus(member.getId(), tokenProvider.createAccessToken(member.getId(), member.getMemberRole().toString(),member.getEmail(),Arrays.asList(new SimpleGrantedAuthority("USER"))));
    }

    @Override
    @Transactional
    public void logout(String accessToken, Member member) {
        redisService.resolveLogout(accessToken);
    }

    /**
     * 폰 인증번호 전송 api
     */
    @Override
    @Transactional
    public Auth sendPhoneAuth(String phone) {

        String code = Integer.toString((int) (Math.random() * 8999) + 1000);

        Boolean expired = false;

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");

        //HttpBody 객체 생성
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("target", phone);
        params.put("password", "junjunjun");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {

            throw new RuntimeException("Json 에러", e);
        }
        HttpEntity<String> phoneAuthRequest = new HttpEntity<>(jsonBody, headers);


        //Http 요청해서 응답 받음
        ResponseEntity<Void> response = rt.exchange(
                "http://116.39.207.35:8000/smsroute",
                HttpMethod.POST,
                phoneAuthRequest,
                Void.class
        );

        Auth auth = MemberConverter.toPhoneAuth(phone, code, expired);
        Optional<Auth> delete = authRepository.findByPhone(phone);

        if (delete.isPresent()) {

            Auth deleteAuth = delete.orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_PHONE_AUTH_ERROR));
            authRepository.delete(deleteAuth);
        }

        authRepository.save(auth);
        return auth;
    }

    @Override
    @Transactional
    public Boolean confirmPhoneAuth(MemberRequestDTO.PhoneAuthConfirmDTO request) {
        Boolean checkPhone = false;
        Auth auth = authRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_PHONE_AUTH_ERROR));
        LocalDateTime current = LocalDateTime.now();
        //인증 코드 같은지 확인, 이미 사용된 인증인지 체크, 만료시간 지났는지 체크
        if (auth.getCode().equals(request.getCode()) && !auth.getExpired() && auth.getExpireDate().isAfter(current)) {
            checkPhone = true;

            //phoneAuth expired true로 update 코드
            auth.useToken();
            authRepository.save(auth);

        } else {
            throw new MemberHandler(ErrorStatus.MEMBER_PHONE_AUTH_ERROR);
        }

        return checkPhone;
    }

    @Override
    public Boolean confirmEmailDuplicate(MemberRequestDTO.EmailDuplicateConfirmDTO request) {

        Optional<Member> member = memberRepository.findByEmail(request.getEmail());
        Boolean checkEmail = false;

        if (!member.isPresent()) {

            checkEmail = true;
        }else{
            if(member.get().getMemberStatus().equals(MemberStatus.INACTIVE)){
                checkEmail =true;
            }
        }
        return checkEmail;
    }

    @Override
    public Boolean confirmNicknameDuplicate(MemberRequestDTO.NicknameDuplicateConfirmDTO request) {
        Optional<Member> member = memberRepository.findByNickname(request.getNickname());

        Boolean checkNickname = false;

        if (!member.isPresent()) {
            checkNickname = true;
        }else{
            if(member.get().getMemberStatus().equals(MemberStatus.INACTIVE)){
                checkNickname =true;
            }
        }
        return checkNickname;
    }

    @Override
    @Transactional
    public String confirmPhoneAuthFindEmail(MemberRequestDTO.PhoneAuthConfirmFindEmailDTO request) {

        Auth auth = authRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_PHONE_AUTH_ERROR));
        String email = "";
        LocalDateTime current = LocalDateTime.now();
        //코드, 만료 됐는지, 만료 시간 체크
        if (auth.getCode().equals(request.getCode()) && !auth.getExpired() && auth.getExpireDate().isAfter(current)) {
            Optional<Member> member = memberRepository.findByPhone(request.getPhone());
            log.info(member.get().getEmail());
            if (member.isPresent()) {
                email = member.get().getEmail();
                //사용한 인증 만료로 변
                auth.useToken();
                authRepository.save(auth);

            } else {
                throw new MemberHandler(ErrorStatus.MEMBER_PHONE_AUTH_ERROR);
            }
        } else {
            throw new MemberHandler(ErrorStatus.MEMBER_PHONE_AUTH_ERROR);
        }
        return email;

    }

    @Override
    @Transactional
    public Auth sendEmailAuth(String email) {

        String code = Integer.toString((int) (Math.random() * 8999) + 1000);
        Boolean expired = false;

        Optional<Member> member = memberRepository.findByEmail(email);

        if (!member.isPresent()) {
            throw new MemberHandler(ErrorStatus.MEMBER_EMAIL_AUTH_ERROR);
        }

        mailConfig.sendMail(email, code);

        Auth auth = MemberConverter.toEmailAuth(email, code, expired);
        Optional<Auth> delete = authRepository.findByEmail(email);

        if (delete.isPresent()) {

            Auth deleteAuth = delete.orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_EMAIL_AUTH_ERROR));
            authRepository.delete(deleteAuth);
        }

        authRepository.save(auth);
        return auth;

    }

    @Override
    @Transactional
    public Boolean confirmEmailAuth(MemberRequestDTO.EmailAuthConfirmDTO request) {
        Boolean checkEmail = false;
        Auth auth = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_EMAIL_AUTH_ERROR));
        LocalDateTime current = LocalDateTime.now();
        //인증 코드 같은지 확인, 이미 사용된 인증인지 체크, 만료시간 지났는지 체크
        if (auth.getCode().equals(request.getCode()) && !auth.getExpired() && auth.getExpireDate().isAfter(current)) {
            checkEmail = true;

            //phoneAuth expired true로 update 코드
            auth.useToken();
            authRepository.save(auth);

        } else {
            throw new MemberHandler(ErrorStatus.MEMBER_EMAIL_AUTH_ERROR);
        }

        return checkEmail;
    }

    @Override
    public MemberResponseDTO.LoginResultDTO emailAuthCreateJWT(MemberRequestDTO.EmailAuthConfirmDTO request, HttpServletResponse response) {

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        // refresh 생성후 쿠키로 만들고 response 헤더에 담기
        RefreshToken refreshToken = redisService.generateRefreshToken(request.getEmail());
        Cookie cookie = new Cookie("refreshToken",refreshToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime accessExpireTime = currentDateTime.plusHours(6);

        return MemberResponseDTO.LoginResultDTO.builder()
                .accessToken(redisService.saveLoginStatus(member.getId(), tokenProvider.createAccessToken(member.getId(), member.getMemberRole().toString() , request.getEmail(), Arrays.asList(new SimpleGrantedAuthority("USER")))))
                .accessExpireTime(accessExpireTime)
                .build();


    }

    @Override
    @Transactional
    public Boolean updatePassword(MemberRequestDTO.PasswordUpdateDTO request, Member member) {
        boolean updatePassword = true;

        Member update = MemberConverter.toUpdatePassword(member, encoder.encode(request.getPassword()));

        memberRepository.save(update);

        return updatePassword;
    }

    @Override
    @Transactional
    public ApiResponse<?> kakaoAuth(String code, HttpServletResponse res) {

        String jwt;

        //Post방식으로 key=value 데이터를 요청
        RestTemplate rt = new RestTemplate();

        //HttpHeader 객체 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 객체 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakao_client_id);
        params.add("redirect_uri", kakao_redirect_uri);
        params.add("code", code);
        //params.add("client_secret","");

        //HttpHeader와 HttpBody를 하나의 객체로 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        //Http 요청해서 응답 받음
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        ///////////////////////////
        //토큰 이용해서 정보 가져오기

        RestTemplate rt2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        //HttpHeader와 HttpBody를 하나의 객체로 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );


        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String phone = kakaoProfile.kakao_account.phone_number.replaceAll("[^0-9]", "");
        if (phone.startsWith("82")) {
            phone = "0" + phone.substring(2);
        }


        Optional<Member> member = memberRepository.findByPhone(phone);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime accessExpireTime = currentDateTime.plusHours(6);



        if (member.isPresent()) {

            if(!this.checkDeregister(member.orElseThrow())){
                throw new MemberHandler(ErrorStatus.MEMBER_INACTIVATE);
            }

            // refresh 생성후 쿠키로 만들고 response 헤더에 담기
            RefreshToken refreshToken = redisService.generateRefreshToken(member.get().getEmail());
            Cookie cookie = new Cookie("refreshToken",refreshToken.getToken());
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            res.addCookie(cookie);


            return ApiResponse.onSuccess(MemberResponseDTO.LoginResultDTO.builder()
                    .accessToken(redisService.saveLoginStatus(member.get().getId(), tokenProvider.createAccessToken(member.get().getId(), member.get().getMemberRole().toString() , member.get().getEmail(), Arrays.asList(new SimpleGrantedAuthority("USER")))))
                    .accessExpireTime(accessExpireTime)
                    .build());

        }

        return ApiResponse.onFailure("MEMBER401", "로그인 실패 회원가입 필요", MemberConverter.toSocialJoinResultDTO(phone, kakaoProfile.getKakao_account().email));



    }

    @Override
    @Transactional
    public ApiResponse<?> naverAuth(String code, String state, HttpServletResponse res) {
        RestTemplate rt = new RestTemplate();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 객체 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naver_client_id);
        params.add("redirect_uri", naver_redirect_uri);
        params.add("client_secret", naver_client_secret);
        params.add("code", code);
        params.add("state", state);


        //HttpHeader와 HttpBody를 하나의 객체로 담기
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
                new HttpEntity<>(params, headers);

        //Http 요청해서 응답 받음
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        RestTemplate rt2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        //HttpHeader와 HttpBody를 하나의 객체로 담기
        HttpEntity<MultiValueMap<String, String>> naverProfileRequest =
                new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = rt2.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverProfileRequest,
                String.class
        );


        ObjectMapper objectMapper2 = new ObjectMapper();
        NaverProfile naverProfile = null;

        try {
            naverProfile = objectMapper2.readValue(response2.getBody(), NaverProfile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String phone = naverProfile.response.mobile.replaceAll("[^0-9]", "");

        Optional<Member> member = memberRepository.findByPhone(phone);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime accessExpireTime = currentDateTime.plusHours(6);

        if (member.isPresent()) {

            if(!this.checkDeregister(member.orElseThrow())){
                throw new MemberHandler(ErrorStatus.MEMBER_INACTIVATE);
            }

            // refresh 생성후 쿠키로 만들고 response 헤더에 담기
            RefreshToken refreshToken = redisService.generateRefreshToken(member.get().getEmail());
            Cookie cookie = new Cookie("refreshToken",refreshToken.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            res.addCookie(cookie);

            return ApiResponse.onSuccess(MemberResponseDTO.LoginResultDTO.builder()
                    .accessToken(redisService.saveLoginStatus(member.get().getId(), tokenProvider.createAccessToken(member.get().getId(), member.get().getMemberRole().toString() , member.get().getEmail(), Arrays.asList(new SimpleGrantedAuthority("USER")))))
                    .accessExpireTime(accessExpireTime)
                    .build());

        }



        return ApiResponse.onFailure("MEMBER401","로그인 실패 회원가입 필요",MemberConverter.toSocialJoinResultDTO(phone, naverProfile.getResponse().email));
    }

    @Override
    @Transactional
    public Member profileModify(MultipartFile profile, String nickname, String introduce, Member member) {

        Optional<ProfileImg> older = profileImgRepository.findByMember_Id(member.getId());
        if (older.isPresent()) {
            profileImgRepository.delete(older.orElseThrow());
        }

        String profileUrl = utilService.uploadS3Img("member", profile);

        ProfileImg profileImg = MemberConverter.toProfileImg(profileUrl, member);
        profileImgRepository.save(profileImg);

        Member update = MemberConverter.toUpdateProfile(member, profileImg, nickname, introduce);
        memberRepository.save(update);


        return member;
    }


    @Override
    public void updatePhoneName(MemberRequestDTO.PhoneNameUpdateDTO request, Member member) {

        memberRepository.changeMemberName(member.getId(),request.getName());

        memberRepository.changeMemberPhone(member.getId(),request.getPhone());

    }

    @Override
    public Address postAddress(MemberRequestDTO.AddressDTO request, Member member) {

        Address address =MemberConverter.toAddress(request,member);
        addressRepository.save(address);
        return address;
    }

    @Override
    public Account postAccount(MemberRequestDTO.AccountDTO request, Member member) {

        Account account =MemberConverter.toAccount(request,member);
        accountRepository.save(account);
        return account;
    }

    @Override
    public void updateAddress(MemberRequestDTO.AddressDTO request, Member member,Long addressId) {

        Address address = addressRepository.findById(addressId).orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_ADDRESS_NOT_FOUND));
        if(!address.getMember().equals(member)){
            throw new MemberHandler(ErrorStatus.MEMBER_NOT_OWNER);
        }
        addressRepository.changeAddress(addressId, request.getAddressName(),request.getAddressSpec(), request.getDeliveryMemo(), request.getZipcode(), request.getDefaultCheck(),request.getRecipientName(),request.getRecipientPhone());


    }

    @Override
    public void updateAccount(MemberRequestDTO.AccountDTO request, Member member, Long accountId) {

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_ACCOUNT_NOT_FOUND));

        if(!account.getMember().equals(member)){
            throw new MemberHandler(ErrorStatus.MEMBER_NOT_OWNER);
        }
        accountRepository.changeAccount(accountId, request.getAccountNum(),request.getBankName(),request.getOwner(),request.getDefaultCheck());

    }

    @Override
    public void deleteAccount(Member member, Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_ACCOUNT_NOT_FOUND));

        if(!account.getMember().equals(member)){
            throw new MemberHandler(ErrorStatus.MEMBER_NOT_OWNER);
        }

        accountRepository.delete(account);
    }

    @Override
    public void deleteAddress(Member member, Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_ADDRESS_NOT_FOUND));

        if(!address.getMember().equals(member)){
            throw new MemberHandler(ErrorStatus.MEMBER_NOT_OWNER);
        }

        addressRepository.delete(address);
    }

    /**
     * 카테고리 validator
     */
    @Override
    public boolean existCategoryById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    @Override
    public boolean existMemberById(Long memberId) {
        return memberRepository.existsById(memberId);
    }

    @Override
    @Transactional
    public void deleteMember(MemberRequestDTO.WithdrawReasonDTO request, Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        WithdrawReason withdrawReason = WithdrawReason.builder()
                .reason(request.getReason())
                .caution(request.getCaution())
                .member(member)
                .build();

        //이미 존재하는 탈퇴사유 삭제
        Optional<WithdrawReason> beforereason = withdrawReasonRepository.findByMember_Id(memberId);
        if(beforereason.isPresent()){
            withdrawReasonRepository.delete(beforereason.get());
        }
        withdrawReasonRepository.save(withdrawReason);
        member.setMemberStatus(MemberStatus.INACTIVE);
        memberRepository.save(member);
        List<Item> itemList = itemRepository.findAllByMember(member);
        itemList.stream().forEach(item -> {
            item.updateStatus(ItemStatus.INACTIVE);
        });
        return;
    }

    public boolean checkDeregister(Member member){

        if(member.getMemberStatus() == MemberStatus.INACTIVE){
            return false;
        }
        else{
            return true;
        }

    }

    @Override
    public void updateNotification(Member member, Integer type) {

        switch(type){
            case 1:
                if(member.getItemNotice()){
                    memberRepository.changeItemNotificationTrue(member.getId());
                }else{
                    memberRepository.changeItemNotificationFalse(member.getId());
                }

                break;
            case 2:
                if(member.getMessageNotice()){
                    memberRepository.changeMessageNotificationTrue(member.getId());
                }else{
                    memberRepository.changeMessageNotificationFalse(member.getId());
                }

                break;
            case 3:
                if(member.getMarketingNotice()){
                    memberRepository.changeMarketingNotificationTrue(member.getId());
                }else{
                    memberRepository.changeMarketingNotificationFalse(member.getId());
                }

                break;
            case 4:
                if(member.getPostNotice()){
                    memberRepository.changePostNotificationTrue(member.getId());
                }else{
                    memberRepository.changePostNotificationFalse(member.getId());
                }

                break;
        }
    }







}

