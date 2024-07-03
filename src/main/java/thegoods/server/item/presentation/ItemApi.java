package thegoods.server.item.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.presentation.ApiResponse;
import thegoods.server.common.validation.annotation.CheckPage;
import thegoods.server.common.validation.annotation.ExistItem;
import thegoods.server.item.converter.ItemConverter;
import thegoods.server.item.domain.Item;
import thegoods.server.item.implement.command.ItemCommandService;
import thegoods.server.item.implement.query.ItemQueryService;
import thegoods.server.item.presentation.dto.ItemRequestDTO;
import thegoods.server.item.presentation.dto.ItemResponseDTO;
import thegoods.server.member.domain.Member;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Item", description = "상품 관련 API")
@RequestMapping("/api")
public class ItemApi {
    private final ItemCommandService itemCommandService;
    private final ItemQueryService itemQueryService;
    private final MemberQueryService memberQueryService;

    @PostMapping("/seller/item")
    @Operation(summary = "상품 등록 API", description = "상품 등록을 위한 API이며, requset 파라미터로 입력 값을 받는다. " +
            "배송 방법(delivery_type) (1~8) : PO, CJ, LOTTE, LOGEN, HANJIN, GS25, CU, ETC")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ItemResponseDTO.UploadItemResultDTO> upload(@RequestPart(value = "request") @Valid ItemRequestDTO.UploadItemDTO request,
                                                                   @RequestPart(value = "itemThumbnail") MultipartFile itemThumbnail,
                                                                   @RequestPart(value = "itemImgList", required = false) List<MultipartFile> itemImgList,
                                                                   Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Item item = itemCommandService.uploadItem(member, request, itemThumbnail, itemImgList);
        return ApiResponse.onSuccess(ItemConverter.toUploadItemResultDTO(item));
    }

    @GetMapping("/item/{itemId}")
    @Operation(summary = "상품 상세 조회 API", description = "상품 상세 정보 조회를 위한 API이며, path variable로 입력 값을 받는다. " +
            "itemId : 조회할 상품의 id")
    @Parameters(value = {
            @Parameter(name = "itemId", description = "조회할 상품의 id 입니다.")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ItemResponseDTO.ItemContentDTO> getItemContent(@ExistItem @PathVariable(name = "itemId") Long itemId, Authentication authentication) {
        Member member;

        if (authentication == null) {
            member = memberQueryService.findMemberByNickname("no_login_user").orElseThrow(() -> new ItemHandler(ErrorStatus.ITEM_VIEW_ERROR));
        } else {
            member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        }

        Item itemContent = itemCommandService.getItemContent(itemId, member);
        return ApiResponse.onSuccess(ItemConverter.getItemContentDTO(itemContent));
    }

    @PutMapping("/seller/item/{itemId}")
    @Operation(summary = "상품 수정 API", description = "상품 수정을 위한 API이며, path variable로 입력 값을 받습니다. " +
            "itemId : 수정할 상품의 id")
    @Parameters(value = {
            @Parameter(name = "itemId", description = "조회할 상품의 id 입니다.")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ItemResponseDTO.UpdateItemResultDTO> updateItem(@RequestPart(value = "request") @Valid ItemRequestDTO.UpdateItemDTO request,
                                                                       @ExistItem @PathVariable(name = "itemId") Long itemId,
                                                                       @RequestPart(value = "itemThumbnail") MultipartFile itemThumbnail,
                                                                       @RequestPart(value = "itemImgList", required = false) List<MultipartFile> itemImgList,
                                                                       Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Item item = itemCommandService.updateItem(itemId, member, request, itemThumbnail, itemImgList);
        return ApiResponse.onSuccess(ItemConverter.toUpdateItemResultDTO(item));
    }

    @GetMapping("/seller/item")
    @Operation(summary = "나의 판매 상품 조회 API", description = "판매중인 상품 조회를 위한 API이며, request parameter로 입력 값을 받습니다. " +
            "page : 상품 조회 페이지 번호")
    @Parameters(value = {
            @Parameter(name = "page", description = "페이지 번호, 1 이상의 숫자를 입력해주세요.")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ItemResponseDTO.ItemPreviewListDTO> getMyItemList(@CheckPage @RequestParam(name = "page") Integer page,
                                                                         Authentication authentication) {
        if (authentication == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Page<Item> itemList = itemQueryService.getMyItemList(member, page - 1);
        return ApiResponse.onSuccess(ItemConverter.itemPreviewListDTO(itemList));
    }


    @GetMapping("/search/item")
    @Operation(summary = "판매 상품 검색 API", description = "상품 검색을 위한 API이며, request parameter로 입력 값을 받습니다. \n\n" +
            "page : 상품 조회 페이지 번호 \n\n itemName : 상품 이름(String) \n\n category : 카테고리 이름(String) \n\n sellerName : 판매자 이름(String) \n\n tagNames : 태그 이름(List(String)) \n\n type: 조회 타입으로, new, popular, dibsCount, salesCount, lowPrice, highPrice, reviewCount 중 하나의 값을 입력해주세요.")
    @Parameters(value = {
            @Parameter(name = "page", description = "페이지 번호, 1 이상의 숫자를 입력해주세요."),
            @Parameter(name = "itemName", description = "상품 이름, 상품 검색이 아닐시 빈칸을 입력해주세요."),
            @Parameter(name = "category", description = "카테고리 이름, 카테고리 검색이 아닐시 빈칸을 입력해주세요."),
            @Parameter(name = "sellerName", description = "판매자 이름, 판매자 검색이 아닐시 빈칸을 입력해주세요."),
            @Parameter(name = "tagNames", description = "태그 이름, 태그 검색이 아닐시 빈칸을 입력해주세요."),
            @Parameter(name = "type", description = "조회 타입으로, new, popular, dibsCount, salesCount, lowPrice, highPrice, reviewCount 중 하나의 값을 입력해주세요.")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ItemResponseDTO.ItemPreviewListDTO> searchItemList(@CheckPage @RequestParam(name = "page") Integer page,
                                                                          @RequestParam(name = "itemName", required = false) String itemName,
                                                                          @RequestParam(name = "category", required = false) String categoryName,
                                                                          @RequestParam(name = "sellerName", required = false) String sellerName,
                                                                          @RequestParam(name = "tagNames", required = false) List<String> tagName,
                                                                          @RequestParam(name = "type") String type,
                                                                          Authentication authentication) {
        Member member;

        if (authentication == null) {
            member = memberQueryService.findMemberByNickname("no_login_user").orElseThrow(() -> new ItemHandler(ErrorStatus.ITEM_VIEW_ERROR));
        } else {
            member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        }

        Page<Item> itemList = itemQueryService.searchItem(member, itemName, categoryName, sellerName, tagName, type, page - 1);

        return ApiResponse.onSuccess(ItemConverter.itemPreviewListDTO(itemList));
    }


    @GetMapping("/item/main")
    @Operation(summary = "신상품/인기상품/마감임박상품 조회 API", description = "신상품/인기상품/마감임박상품 조회 API입니다. \n\n" +
            "page : 상품 조회 페이지 번호 \n\n type : 조회 타입 (신상품/인기상품/마감임박상품)")
    @Parameters(value = {
            @Parameter(name = "page", description = "페이지 번호, 1 이상의 숫자를 입력해주세요."),
            @Parameter(name = "type", description = "조회 타입으로, new, popular, last 중 하나의 값을 입력해주세요.")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ItemResponseDTO.ItemPreviewListDTO> getMainItem(
            @RequestParam(name = "type") String type,
            @CheckPage @RequestParam(name = "page") Integer page
    ) {
        Page<Item> itemList = itemQueryService.getMainItem(type, page - 1);

        return ApiResponse.onSuccess(ItemConverter.itemPreviewListDTO(itemList));
    }

    @GetMapping("/item/{itemId}/related")
    @Operation(summary = "관련 상품 조회 API", description = "해당 상품과 같은 카테고리를 갖는 상품을 조회하는 API 입니다. \n\n" +
            "page : 상품 조회 페이지 번호 \n\n" +
            "itemId : 상품 아이디")
    @Parameters(value = {
            @Parameter(name = "itemId", description = "상품 id"),
            @Parameter(name = "page", description = "페이지 번호, 1 이상의 숫자를 입력해주세요."),
    })
    public ApiResponse<ItemResponseDTO.ItemPreviewListDTO> getRelatedItem(
            @ExistItem @PathVariable(name = "itemId") Long itemId,
            @CheckPage @RequestParam(name = "page") Integer page
    ) {

        Page<Item> itemList = itemQueryService.getRelatedItem(itemId, page - 1);

        return ApiResponse.onSuccess(ItemConverter.itemPreviewListDTO(itemList));
    }
}
