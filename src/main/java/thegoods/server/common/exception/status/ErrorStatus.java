package thegoods.server.common.exception.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import thegoods.server.common.exception.BaseErrorCode;
import thegoods.server.common.exception.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 상품 관련 에러
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM4001", "해당 상품을 찾을 수 없습니다."),
    ITEMOPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM4002", "해당 상품 옵션을 찾을 수 없습니다."),
    ITEMOPTION_NOT_MATCH(HttpStatus.BAD_REQUEST, "ITEM4003", "해당 상품의 옵션이 아닙니다."),
    ITEM_UPDATE_FAIL(HttpStatus.BAD_REQUEST, "ITEM4004", "상품 정보 변경 권한이 없습니다."),
    ITEM_VIEW_ERROR(HttpStatus.BAD_REQUEST, "ITEM4005", "비회원 상품 조회 권한이 없습니다."),
    ITEM_SEARCH_ERROR(HttpStatus.BAD_REQUEST, "ITEM4006", "잘못된 검색어가 입력 되었습니다. 검색 조건은 하나만 가능합니다."),
    ITEM_NOT_SELLER(HttpStatus.BAD_REQUEST, "ITEM4007", "회원이 판매자가 아닙니다."),
    MAIN_ITEM_SEARCH_TYPE_ERROR(HttpStatus.BAD_REQUEST, "ITEM4008", "잘못된 메인 상품 조회 타입 입니다."),

    // 주문 관련 에러
    LACK_OF_STOCK(HttpStatus.BAD_REQUEST, "ORDER4001", "재고가 부족합니다."),
    NULL_ITEMOPTION_ERROR(HttpStatus.BAD_REQUEST, "ORDER4002", "주문할 상품 옵션을 선택해주세요."),
    NO_LOGIN_ORDER_NOT_AVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR, "ORDER4003", "비회원 주문 불가, 관리자에게 문의 바랍니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER4004", "해당 주문 내역을 찾을 수 없습니다"),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER4005", "해당 주문 상품 내역을 찾을 수 없습니다."),
    NOT_ORDER_OWNER(HttpStatus.BAD_REQUEST, "ORDER4006", "본인의 주문 내역이 아닙니다. 접근할 수 없습니다."),
    NO_LOGIN_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER4007", "비회원 주문 내역을 찾을 수 없습니다"),
    ORDER_ITEM_UPDATE_FAIL(HttpStatus.BAD_REQUEST, "ORDER4008", "주문 상품 정보 변경이 불가합니다."),
    NULL_AMOUNT_ERROR(HttpStatus.BAD_REQUEST, "ORDER4009", "주문 수량은 null일 수 없습니다."),

    // 장바구니 관련 에러
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART4001", "해당 장바구니 내역을 찾을 수 없습니다."),
    NOT_CART_OWNER(HttpStatus.BAD_REQUEST, "CART4002", "본인의 장바구니 내역이 아닙니다. 접근할 수 없습니다."),
    CART_DETAIL_FOUND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CART4003", "단일 상품의 장바구니 상세 내역을 찾을 수 없습니다. 관리자에게 문의 바랍니다."),
    CART_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "CART4004", "해당 장바구니 상세 내역을 찾을 수 없습니다"),
    DELETE_CART_DETAIL_FAILED(HttpStatus.BAD_REQUEST, "CART4005", "장바구니 상세 내역을 삭제할 수 없습니다."),

    // 리뷰 관련 에러
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "REVIEW4001", "이미 리뷰를 작성한 주문내역 입니다."),

    // test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "테스트"),

    // Member
    MEMBER_NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "MEMBER4001", "중복된 닉네임 입니다."),
    MEMBER_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "MEMBER4002", "비밀번호가 잘못되었습니다."),
    MEMBER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4003", "이메일이 존재하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4004", "해당 회원을 찾을 수 없습니다."),
    MEMBER_PHONE_AUTH_ERROR(HttpStatus.BAD_REQUEST, "MEMBER4005", "유효하지 않는 번호입니다."),
    MEMBER_EMAIL_AUTH_ERROR(HttpStatus.BAD_REQUEST, "MEMBER4006", "유효하지 않는 이메일입니다."),
    MEMBER_PASSWORD_NOT_EQUAL(HttpStatus.BAD_REQUEST, "MEMBER4007", "비밀번호가 일치하지 않습니다."),
    MEMBER_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4008", "계좌가 존재하지 않습니다"),
    MEMBER_ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4009", "주소가 존재하지 않습니다"),
    MEMBER_INACTIVATE(HttpStatus.NOT_ACCEPTABLE, "MEMBER4010", "탈퇴한 회원입니다."),
    MEMBER_NOT_OWNER(HttpStatus.NOT_ACCEPTABLE, "MEMBER4011", "해당 회원이 아닙니다."),
    MEMBER_CONTACT_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4012", "연락 가능 시간을 조회할 수 없습니다."),
    MEMBER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "MEMBER4013", "중복된 이메일입니다."),
    MEMBER_EMAIL_INCORRECT(HttpStatus.BAD_REQUEST, "MEMBER4014", "잘못된 이메일입니다."),

    //JWT

    JWT_BAD_REQUEST(HttpStatus.UNAUTHORIZED, "JWT4001", "잘못된 JWT 서명입니다."),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4002", "액세스 토큰이 만료되었습니다."),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4003", "리프레시 토큰이 만료되었습니다. 다시 로그인하시기 바랍니다."),
    JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT4004", "지원하지 않는 JWT 토큰입니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT4005", "유효한 JWT 토큰이 없습니다."),

    //POST

    POST_SELF_FOLLOW(HttpStatus.BAD_REQUEST, "POST4001", "자신을 팔로우 했습니다."),
    POST_ALREADY_FOLLOW(HttpStatus.BAD_REQUEST, "POST4002", "이미 팔로우 했습니다."),
    POST_FOLLOW_NOT_FOUND(HttpStatus.BAD_REQUEST, "POST4003", "해당 팔로우를 찾을수 없습니다."),
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "POST4004", "포스트를 찾을수 없습니다."),
    POST_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "POST4005", "해당 댓글을 찾을 수 없습니다."),
    POST_COMMENT_NOT_UPDATE(HttpStatus.BAD_REQUEST, "POST4006", "해당 댓글을 수정할 수 없습니다."),


    //Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY4001", "해당 카테고리가 존재하지 않습니다"),

    //Term
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "TERM4001", "해당 약관이 존재하지 않습니다."),

    //Declaration
    DECLARE_NOT_FOUND(HttpStatus.NOT_FOUND, "DECLARE4001", "해당 신고 내역이 존재하지 않습니다."),

    //ProfileImg
    PROFILEIMG_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILEIMG4001", "해당 프로필 이미지가 존재하지 않습니다."),

    //tag error
    TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "TAG4001", "존재하지 않는 태그입니다."),

    // 페이징 관련 에러
    PAGE_NEGATIVE_INPUT(HttpStatus.BAD_REQUEST, "PAGE4001", "페이지 번호는 1이상의 숫자여야 합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}

