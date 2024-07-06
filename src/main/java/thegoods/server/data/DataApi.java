package thegoods.server.data;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import thegoods.server.common.presentation.ApiResponse;
import thegoods.server.data.dto.DataRequestDTO;
import thegoods.server.data.dto.DataResponseDTO;
import thegoods.server.data.implement.DataCommandService;
import thegoods.server.member.domain.Member;
import thegoods.server.common.enums.Gender;
import thegoods.server.item.domain.Item;
import thegoods.server.member.implement.command.MemberCommandServiceImpl;
import thegoods.server.member.implement.query.MemberQueryService;
import thegoods.server.member.presentation.dto.MemberRequestDTO;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Slf4j
public class DataApi {
    private final MemberCommandServiceImpl memberCommandService;
    private final MemberQueryService memberQueryService;
    private final DataCommandService dataCommandService;


    @PostMapping(value = "/setItemData")
    @Operation(summary = "상품 크롤링 데이터 추가 API", description = "크롤링된 상품 데이터를 db에 저장하는 API 입니다. Swagger에서는 테스트가 불가능합니다. ")
    public ApiResponse<DataResponseDTO.addItemDTO> setItemData(
            @RequestPart(value = "request") @Valid DataRequestDTO.setItemDTO request,
            @RequestPart(value = "itemThumbnail") MultipartFile itemThumbnail,
            @RequestPart(value = "itemImgList", required = false) List<MultipartFile> itemImgList
            //@RequestPart(value = "sellerProfile") MultipartFile sellerProfile
    ) {
        log.info("=========================== 회원 찾기 시작 =======================");

        // 회원 가입
        Optional<Member> member = memberQueryService.findMemberByNickname(request.getSellerName());
        Member seller = null;
        if (member.isEmpty()) { // 해당 판매자 회원이 없으면
            log.info("=========================== 닉네임으로 회원 찾을 수 없음 ================================");

            // 약관 동의 리스트 설정
            List<Boolean> termAgreeList = new ArrayList<>();
            termAgreeList.add(true);
            termAgreeList.add(true);
            termAgreeList.add(true);
            termAgreeList.add(true);


            // 선호 카테고리 랜덤 설정
            List<Long> memberCategoryList = new ArrayList<>();
            long leftLimit = 1L;
            long rightLimit = 4L;
            long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
            memberCategoryList.add(generatedLong);

            // 회원 가입
            DataRequestDTO.setMemberDTO sellerDTO = new DataRequestDTO.setMemberDTO(request.sellerName, request.itemUuid, termAgreeList, memberCategoryList);
            seller = dataCommandService.addMember(sellerDTO);
        } else {
            log.info("=========================== 닉네임으로 회원 찾음 ================================");

            seller = memberQueryService.findMemberByNickname(request.getSellerName()).get();
        }

        // 상품 등록
        Item item = dataCommandService.addItem(seller, request, itemThumbnail, itemImgList);

        return ApiResponse.onSuccess(DataConverter.toAddItemDTO(item));
    }

    @GetMapping("/test/hello")
    @Operation(summary = "Return hello", description = "simple API for swagger test!")
    public String hello() {
        return "Hello, Swagger!";
    }

    @GetMapping("/setTestData")
    @Operation(summary = "테스트용 데이터 생성 API", description = "회원 dummy data를 생성해 db에 저장합니다. category, term, tag 데이터는 db에 수동으로 입력해야 합니다. (id가 1인 category, term, tag 데이터가 필요합니다.) \n\n " +
            "category 생성 sql: INSERT INTO category(name,created_at,updated_at) VALUES ('아이돌',now(),now());\n\n" +
            "tag 생성 sql: INSERT INTO tag(name,created_at,updated_at) VALUES ('포카',now(),now());\n\n" +
            "term 생성 sql: INSERT INTO term(title,created_at,updated_at) VALUES ('정보 이용 동의',now(),now());\n\n" +
            "생성된 회원 계정은 아래와 같습니다.\n\n" +
            "1. 구매자 계정 - email: test@gmail.com, password: 12345678\n\n" +
            "2. 비로그인 주문용 계정 - email: nologinuser@gmail.com, password:12345678")
    public String setTestData() {

        // 회원 가입
        Date date = new Date();
        List<Boolean> termAgreeList = new ArrayList<>();
        termAgreeList.add(true);

        MemberRequestDTO.JoinDTO joinBuyerDTO = new MemberRequestDTO.JoinDTO("구매자 테스트 계정", "테스트", "12345678", "test@gmail.com", date, "01012345678", Gender.MALE, termAgreeList);
        MemberRequestDTO.JoinDTO noLoginUserDTO = new MemberRequestDTO.JoinDTO("no_login_user", "비회원", "12345678", "nologinuser@gmail.com", date, "01087654321", Gender.MALE, termAgreeList);


        memberCommandService.join(joinBuyerDTO);
        memberCommandService.join(noLoginUserDTO);

        return " Test Data Set Completed";
    }
}
