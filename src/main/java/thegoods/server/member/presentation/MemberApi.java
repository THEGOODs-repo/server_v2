package thegoods.server.member.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.exception.status.SuccessStatus;
import thegoods.server.common.presentation.ApiResponse;
import thegoods.server.member.converter.MemberConverter;
import thegoods.server.member.domain.*;
import thegoods.server.member.exception.handler.MemberHandler;
import thegoods.server.member.implement.command.MemberCommandService;
import thegoods.server.member.implement.query.MemberQueryService;
import thegoods.server.member.presentation.dto.MemberRequestDTO;
import thegoods.server.member.presentation.dto.MemberResponseDTO;
import thegoods.server.redis.domain.RefreshToken;
import thegoods.server.redis.service.RedisService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Member", description = "Member 관련 API")
@RequestMapping("/api/members")
public class MemberApi {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final RedisService redisService;


    @PostMapping("/join")
    @Operation(summary = "회원가입 API", description = "request 파라미터 : 닉네임, 비밀번호(String), 이메일, 생일(yyyymmdd), 성별(MALE, FEMALE, NO_SELECET), 폰번호(010xxxxxxxx),이용약관(Boolean 배열), 카테고리(Long 배열)")
    public ApiResponse<MemberResponseDTO.JoinResultDTO> join(@RequestBody @Valid MemberRequestDTO.JoinDTO request) {
        Member member = memberCommandService.join(request);

        return ApiResponse.onSuccess(MemberConverter.toJoinResultDTO(member));
    }

    @PostMapping("/phone/auth")
    @Operation(summary = "휴대폰 인증 번호 요청 API", description = "request: 휴대폰 번호, response : 인증번호")
    public ApiResponse<MemberResponseDTO.PhoneAuthSendResultDTO> phoneAuthSend(@RequestBody MemberRequestDTO.PhoneAuthDTO request) throws JsonProcessingException {

        Auth auth = memberCommandService.sendPhoneAuth(request.getPhone());

        return ApiResponse.onSuccess(MemberConverter.toPhoneAuthSendResultDTO(auth));
    }

    @PostMapping("phone/auth/verify")
    @Operation(summary = "회원가입시 휴대폰 인증 번호 확인 API", description = "request: 인증 코드, response : 인증완료 true")
    public ApiResponse<MemberResponseDTO.PhoneAuthConfirmResultDTO> phoneAuth(@RequestBody MemberRequestDTO.PhoneAuthConfirmDTO request) {
        Boolean checkPhone = memberCommandService.confirmPhoneAuth(request);

        return ApiResponse.onSuccess(MemberConverter.toPhoneAuthConfirmResultDTO(checkPhone));
    }

    @PostMapping("email/duplicate")
    @Operation(summary = "이메일 중복 체크 API", description = "request : 이메일, response : 중복이면 false, 중복이 아니면 true")
    public ApiResponse<MemberResponseDTO.EmailDuplicateConfirmResultDTO> emailDuplicate(@RequestBody MemberRequestDTO.EmailDuplicateConfirmDTO request) {
        Boolean checkEmail = memberCommandService.confirmEmailDuplicate(request);

        return ApiResponse.onSuccess(MemberConverter.toEmailDuplicateConfirmResultDTO(checkEmail));
    }

    @PostMapping("nickname/duplicate")
    @Operation(summary = "닉네임 중복 체크 API", description = "request : 닉네임, response: 중복이면 false, 중복 아니면 true")
    public ApiResponse<MemberResponseDTO.NicknameDuplicateConfirmResultDTO> nicknameDuplicate(@RequestBody MemberRequestDTO.NicknameDuplicateConfirmDTO request) {
        Boolean checkNickname = memberCommandService.confirmNicknameDuplicate(request);

        return ApiResponse.onSuccess(MemberConverter.toNicknameDuplicateConfirmResultDTO(checkNickname));
    }

    @DeleteMapping(value = "/delete")
    @Operation(summary = "회원 탈퇴 api", description = "request: 회원 탈퇴 사유 번호로 주시면 됩니다")
    public ApiResponse<?> deleteMember(@RequestBody MemberRequestDTO.WithdrawReasonDTO request,
                                       Authentication authentication){


        memberCommandService.deleteMember(request, Long.valueOf(authentication.getName().toString()));
        return ApiResponse.of(SuccessStatus.MEMBER_DELETE_SUCCESS, null);
    }

    //login
    //username
    //password
    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "request 파라미터 : 이메일, 비밀번호(String)")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> login(@RequestBody MemberRequestDTO.LoginDTO request, HttpServletResponse response) {


        return ApiResponse.onSuccess(memberCommandService.login(request,response));
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃 API 입니다.")
    @PostMapping("/logout")
    public ApiResponse<MemberResponseDTO.LogoutResultDTO> logout(Authentication authentication,
                                                                 HttpServletRequest authorizationHeader) {
        String token = authorizationHeader.getHeader("Authorization").substring(7);
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        memberCommandService.logout(token, member);
        return ApiResponse.onSuccess(MemberConverter.toLogoutResultDTO(member));
    }

    @Operation(summary = "리프레쉬 토큰을 이용해 accessToken 재발급 API ️", description = "리프레쉬 토큰으로 accessToken 재발급하는 API입니다.")
    @PostMapping("/token/regenerate")
    public ApiResponse<MemberResponseDTO.NewTokenDTO> getNewToken(@RequestBody MemberRequestDTO.RefreshTokenDTO request,
                                                                  HttpServletResponse response) {
        RefreshToken newRefreshToken = redisService.reGenerateRefreshToken(request);
        String accessToken = memberCommandService.regenerateAccessToken(newRefreshToken);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime accessExpireTime = currentDateTime.plusHours(6);

        Cookie cookie = new Cookie("refreshToken", newRefreshToken.getToken());
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return ApiResponse.onSuccess(MemberConverter.toNewTokenDTO(accessToken,accessExpireTime));
    }
/*
    @PostMapping("/jwt/test")
    @Operation(summary = "jwt test API", description = "테스트 용도 api")
    public ResponseEntity<?> jwtTest(Authentication authentication) {
        //request값으로 Bearer {jwt} 값을 넘겨주면 jwt를 해석해서 Authentication에 정보가 담기고 담긴 정보를 가공해서 사용
        //jwt 토큰은 회원가입하고 로그인하면 발급받을 수 있습니다.
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        return ResponseEntity.ok().body(member.getEmail());
    }
*/

    @PostMapping("password/update")
    @Operation(summary = "새로운 비밀번호 설정 api", description = "request: 비밀번호, 비밀번호 확인 response: 변경완료 true")
    public ApiResponse<MemberResponseDTO.PasswordUpdateResultDTO> updatePassword(@RequestBody MemberRequestDTO.PasswordUpdateDTO request, Authentication authentication) {

        if (request.getPassword() != request.getCheckPassword()) {
            new MemberHandler(ErrorStatus.MEMBER_PASSWORD_NOT_EQUAL);
        }

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Boolean updatePassword = memberCommandService.updatePassword(request, member);

        return ApiResponse.onSuccess(MemberConverter.toPasswordUpdateResultDTO(updatePassword));
    }


    @GetMapping("/kakao/callback")
    @Operation(summary = "카카오 소셜 로그인 api", description = "callback 용도 api여서 swagger에서 test 안됩니다")
    public ApiResponse<?> kakaoCallback(@RequestParam String code, HttpServletResponse response) {

        return memberCommandService.kakaoAuth(code, response);

        //반환한 카카오 프로필에서 기존 회원이면 jwt 토큰 반환 아니면 회원가입 진행
        //토큰 반환은 쉽지만 회원가입 로직으로 보내야하면 false 반환해서 회원가입 진행하도록하기


    }

    @GetMapping("/naver/callback")
    @Operation(summary = "네이버 소셜 로그인 api", description = "callback 용도 api여서 swagger에서 test 안됩니다")
    public ApiResponse<?> naverCallback(@RequestParam String code, String state, HttpServletResponse response) {

        return memberCommandService.naverAuth(code, state, response);

    }



    @PostMapping("phone/auth/verify/find/email")
    @Operation(summary = "이메일 찾기에서 사용되는 번호 확인 api", description = "request: 인증 코드, response: 인증 완료 되면 email")
    public ApiResponse<MemberResponseDTO.PhoneAuthConfirmFindEmailResultDTO> phoneAuthFindEmail(@RequestBody MemberRequestDTO.PhoneAuthConfirmFindEmailDTO request) {
        String email = memberCommandService.confirmPhoneAuthFindEmail(request);

        Member member = memberQueryService.findMemberByEmail(email).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Optional<ProfileImg> profileImg = memberQueryService.findProfileImgByMember(member.getId());

        if(profileImg.isEmpty()){
            return ApiResponse.onSuccess(MemberConverter.toPhoneAuthConfirmFindEmailDTO(email, null));
        }
        else {
            return ApiResponse.onSuccess(MemberConverter.toPhoneAuthConfirmFindEmailDTO(email, profileImg.get().getUrl()));
        }
    }

    @PostMapping("email/auth")
    @Operation(summary = "비밀번호 찾기에서 사용되는 email 인증 요청 api", description = "request: 이메일 입력하면 해당 이메일로 인증번호 전송")
    public ApiResponse<MemberResponseDTO.EmailAuthSendResultDTO> emailAuthSend(@RequestBody MemberRequestDTO.EmailAuthDTO request) {

        Auth auth = memberCommandService.sendEmailAuth(request.getEmail());
        return ApiResponse.onSuccess(MemberConverter.toEmailAuthSendResultDTO(auth));
    }

    @PostMapping("email/auth/verify")
    @Operation(summary = "비밀번호 찾기에서 사용되는 email 인증 코드 검증 api", description = "request: 이메일, 코드 response: 인증완료 true")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> emailAuth(@RequestBody MemberRequestDTO.EmailAuthConfirmDTO request, HttpServletResponse response) {
        Boolean checkEmail = memberCommandService.confirmEmailAuth(request);


        if (checkEmail == true) {

            return ApiResponse.onSuccess(memberCommandService.emailAuthCreateJWT(request,response));
        }

        return ApiResponse.onFailure(ErrorStatus.MEMBER_EMAIL_INCORRECT.getCode(),ErrorStatus.MEMBER_EMAIL_INCORRECT.getMessage(), null);

    }


    @GetMapping(value = "/profile")
    @Operation(summary = "프로필 조회 api", description = "프로필이미지, 닉네임, 주소, 계좌, 팔로잉 수, 찜 수를 조회할 수 있습니다.")
    public ApiResponse<MemberResponseDTO.ProfileResultDTO> getProfile(Authentication authentication) {



        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Optional<ProfileImg> profileImg = memberQueryService.findProfileImgByMember(member.getId());
        List<Address> address = memberQueryService.findAllAddressById(member.getId());
        List<Account> account = memberQueryService.findAllAccountById(member.getId());
        Long following = Long.valueOf(member.getFollowingList().size());
        Long dibs = Long.valueOf(member.getDibsList().size());



        if(profileImg.isEmpty()){
            return ApiResponse.onSuccess(MemberConverter.toProfile(member, null, account, address,following,dibs));
        }
        else {
            return ApiResponse.onSuccess(MemberConverter.toProfile(member, profileImg.get().getUrl(),account, address,following,dibs));
        }


    }

    @PutMapping(value = "/profile/modify", consumes = "multipart/form-data")
    @Operation(summary = "마이페이지 프로필 수정(닉네임, 프로필 사진, 소개) api", description = "request : 프로필 이미지, 닉네임, 자기소개 ")
    public ApiResponse<MemberResponseDTO.ProfileModifyResultDTO> profileModify(@RequestParam("profile") MultipartFile profile,
                                                                               @RequestParam("nickname") String nickname,
                                                                               @RequestParam("introduce") String introduce,
                                                                               Authentication authentication) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));


        Member modifyMember = memberCommandService.profileModify(profile, nickname, introduce, member);

        return ApiResponse.onSuccess(MemberConverter.toProfileModify(modifyMember));
    }





    @PostMapping(value = "/address")
    @Operation(summary = "회원 배송지 추가 api", description = "request: 우편번호, 배송지명, 배송지, 배송메모")
    public ApiResponse<MemberResponseDTO.AddressResultDTO> postAddress(@RequestBody MemberRequestDTO.AddressDTO request, Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Address address = memberCommandService.postAddress(request,member);

        return ApiResponse.onSuccess(MemberConverter.toPostAddressDTO(address.getAddressName()));
    }

    @PutMapping(value = "/address/update/{addressId}")
    @Operation(summary = "회원 배송지 수정 api", description = "request: 우편번호, 배송지명, 배송지, 배송메모")
    @Parameters(value = {
            @Parameter(name = "addressId", description = "주소 id 입니다.")
    })
    public ApiResponse<MemberResponseDTO.AddressResultDTO> updateAddress(@RequestBody MemberRequestDTO.AddressDTO request,
                                                                         @PathVariable (name = "addressId") Long addressId,
                                                                         Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        memberCommandService.updateAddress(request, member, addressId);

        return ApiResponse.onSuccess(null);
    }

    @DeleteMapping(value = "/address/delete/{addressId}")
    @Operation(summary = "회원 배송지 삭제 api", description = "배송지를 삭제하는 api입니다.")
    @Parameters(value = {
            @Parameter(name = "addressId", description = "배송지 id 입니다.")
    })
    public ApiResponse<?> deleteAddress(Authentication authentication,
                                        @PathVariable(name = "addressId") Long addressId){
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        memberCommandService.deleteAddress(member, addressId);
        return ApiResponse.of(SuccessStatus.MEMBER_ADDRESS_DELETE,null);
    }


    @PostMapping(value = "/account")
    @Operation(summary = "회원 계좌 추가 api", description = "request: 소유주 이름, 은행 이름, 계좌번호")
    public ApiResponse<MemberResponseDTO.AccountResultDTO> postAccount(@RequestBody MemberRequestDTO.AccountDTO request, Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Account account = memberCommandService.postAccount(request,member);

        return ApiResponse.onSuccess(MemberConverter.toPostAccountDTO(account.getOwner()));
    }



    @PutMapping(value = "/account/update/{accountId}")
    @Operation(summary = "회원 계좌 수정 api", description = "request: 소유주 이름, 은행 이름, 계좌번호")
    @Parameters(value = {
            @Parameter(name = "accountId", description = "계좌 id 입니다.")
    })
    public ApiResponse<?> updateAccount(@RequestBody MemberRequestDTO.AccountDTO request,
                                        @PathVariable (name = "accountId") Long accountId,
                                        Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        memberCommandService.updateAccount(request,member, accountId);

        return ApiResponse.of(SuccessStatus.MEMBER_ACCOUNT_UPDATE,null);
    }


    @DeleteMapping(value = "/account/delete/{accountId}")
    @Operation(summary = "회원 계좌 삭제 api", description = "계좌를 삭제하는 api입니다. ")
    @Parameters(value = {
            @Parameter(name = "accountId", description = "계좌 id 입니다.")
    })
    public ApiResponse<?> deleteAccount(Authentication authentication,
                                        @PathVariable(name = "accountId") Long accountId){
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        memberCommandService.deleteAccount(member, accountId);
        return ApiResponse.of(SuccessStatus.MEMBER_ACCOUNT_DELETE,null);
    }

    @PutMapping(value = "/notification/update")
    @Operation(summary="mypage 알림 설정 변경 api", description = "마이페이지에서 알림을 on/off할 수 있는 api입니다\n"+
            "request parameter에 알림 타입을 넣어줘야합니다.")
    @Parameter(name = "type", description = "1: 아이템 알림, 2: 메세지 알림, 3: 마케팅 알림, 4: 포스트 알림")
    public ApiResponse<?> updateNotification(Authentication authentication, @RequestParam Integer type){
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        memberCommandService.updateNotification(member,type);
        return ApiResponse.of(SuccessStatus.MEMBER_NOTIFICATION_UPDATE,null);
    }


    @GetMapping(value = "/account")
    @Operation(summary = "mypage 계좌 조회 api")
    public ApiResponse<List<MemberResponseDTO.AccountDTO>> getAccount(Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Account> account = memberQueryService.findAccountById(member.getId());

        return ApiResponse.onSuccess(MemberConverter.toGetAccountDTO(account));
    }

    @GetMapping(value = "/address")
    @Operation(summary = "mypage 배송지 조회 api")
    public ApiResponse<List<MemberResponseDTO.AddressDTO>> getAddress(Authentication authentication){
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Address> addressList = memberQueryService.findAllAddressById(member.getId());

        return ApiResponse.onSuccess(MemberConverter.toGetAddressDTO(addressList));
    }


    @PutMapping(value = "/phone/name/update")
    @Operation(summary = "주문시 고객 정보 수정(이름, 번호) api", description = "이름과 번호 수정할 수 있는 기능")
    public ApiResponse<MemberResponseDTO.PhoneNameUpdateResultDTO> updateRole(@RequestBody MemberRequestDTO.PhoneNameUpdateDTO request, Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        memberCommandService.updatePhoneName(request, member);



        return ApiResponse.onSuccess(null);
    }
}
