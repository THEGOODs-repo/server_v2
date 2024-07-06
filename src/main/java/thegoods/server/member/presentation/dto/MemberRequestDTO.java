package thegoods.server.member.presentation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thegoods.server.common.enums.Gender;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

public class MemberRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDTO {
        @NotBlank(message = "닉네임을 입력해주세요")
        private String nickname;
        @NotBlank(message = "이름을 입력해주세요")
        private String name;
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문, 숫자, 특수문자를 사용하세요.")
        private String password;
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
        private Date birthday;
        @NotBlank(message = "휴대폰 번호를 입력해주세요")
        private String phone;
        private Gender gender;
        private List<Boolean> memberTerm;//약관 동의


    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        private String email;
        private String password;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneAuthDTO {
        private String phone;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneAuthConfirmDTO {
        private String phone;
        private String code;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailDuplicateConfirmDTO {
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NicknameDuplicateConfirmDTO {
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneAuthConfirmFindEmailDTO {
        private String phone;
        private String code;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailAuthDTO {
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailAuthConfirmDTO {
        private String email;
        private String code;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordUpdateDTO {
        private String password;
        private String checkPassword;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneNameUpdateDTO {
        private String name;
        private String phone;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {

        private String addressName;
        private String addressSpec;
        private String deliveryMemo;
        private String zipcode;
        private String recipientName;
        private String recipientPhone;
        private Boolean defaultCheck;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDTO {

        private String owner;
        private String bankName;
        private String accountNum;
        private Boolean defaultCheck;
    }

    @Getter
    @Setter
    public static class RefreshTokenDTO {
        String refreshToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawReasonDTO {
        Integer reason;
        Boolean caution;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeclareDTO{
        //접수 항목
        Integer receipt;
        //판매 항목
        String salePost;
        //신고 이유
        String reason;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactDTO{
        Integer start;
        Integer end;
        boolean all;
    }



}
