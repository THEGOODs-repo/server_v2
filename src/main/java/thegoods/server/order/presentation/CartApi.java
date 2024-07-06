package thegoods.server.order.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.presentation.ApiResponse;
import thegoods.server.member.domain.Member;
import thegoods.server.member.implement.query.MemberQueryService;
import thegoods.server.order.converter.CartConverter;
import thegoods.server.order.domain.Cart;
import thegoods.server.order.implement.command.CartCommandService;
import thegoods.server.order.implement.query.CartQueryService;
import thegoods.server.order.presentation.dto.CartRequestDTO;
import thegoods.server.order.presentation.dto.CartResponseDTO;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Tag(name = "Cart", description = "장바구니 관련 API")
//@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartApi {

    private final MemberQueryService memberQueryService;
    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    @PostMapping
    @Operation(summary = "장바구니 담기 API", description = "해당 상품/옵션을 장바구니에 추가하는 API 입니다.\n\n" +
            "상품이 단일 상품인 경우, ItemOptionId는 제외하고 보내주세요.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<String> add(@RequestBody @Valid CartRequestDTO.cartAddDTOList request,
                                   Authentication authentication) {
        // 비회원인 경우 처리 불가
        if (authentication == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // request에서 member id 추출해 Member 엔티티 찾기
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        cartCommandService.addCart(request, member);

        return ApiResponse.onSuccess("장바구니 담기 성공");
    }

    @GetMapping
    @Operation(summary = "나의 장바구니 목록 조회 API", description = "나의 장바구니 목록을 조회하는 API 입니다. (구매자 회원용)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<CartResponseDTO.cartViewListDTO> cartView(Authentication authentication) {
        // 비회원인 경우 처리 불가
        if (authentication == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // request에서 member id 추출해 Member 엔티티 찾기
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Cart> cartList = cartQueryService.getCartsByMember(member);

        return ApiResponse.onSuccess(CartConverter.toCartViewListDTO(cartList));
    }

    @PutMapping
    @Operation(summary = "장바구니 수량 수정 API", description = "해당 장바구니 내역의 담은 수량을 수정하는 API 입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<String> update(@RequestBody @Valid CartRequestDTO.cartUpdateDTOList request,
                                      Authentication authentication) {
        // 비회원인 경우 처리 불가
        if (authentication == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // request에서 member id 추출해 Member 엔티티 찾기
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        cartCommandService.updateCart(request, member);

        return ApiResponse.onSuccess("장바구니 옵션 수정 성공");
    }

    @DeleteMapping("/detail/delete")
    @Operation(summary = "장바구니 옵션 삭제 API", description = "해당 장바구니 내역의 담은 옵션을 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<String> deleteCartOption(@RequestBody CartRequestDTO.cartOptionDeleteDTO request,
                                                Authentication authentication) {
        // 비회원인 경우 처리 불가
        if (authentication == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // request에서 member id 추출해 Member 엔티티 찾기
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        cartCommandService.deleteCart(request, member);

        return ApiResponse.onSuccess("장바구니 옵션 삭제 성공");
    }

    @DeleteMapping("/delete")
    @Operation(summary = "장바구니 상품 삭제 API", description = "해당 장바구니 담은 상품을 삭제하는 API 입니다. \n\n" +
            "삭제할 상품의 id 리스트를 보내주세요.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<String> deleteCart(@RequestBody CartRequestDTO.cartDeleteByItemDTO request,
                                          Authentication authentication) {

        // 비회원인 경우 처리 불가
        if (authentication == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // request에서 member id 추출해 Member 엔티티 찾기
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        cartCommandService.deleteCartByItem(request, member);

        return ApiResponse.onSuccess("장바구니 상품 삭제 성공");
    }

    @GetMapping("/{itemId}/stock")
    @Operation(summary = "특정 장바구니 상품의 재고/옵션의 재고 목록 조회 API", description = "장바구니에 담긴 특정 상품의 재고 및 옵션의 재고 목록을 조회하는 API 입니다.")
    @Parameters(value = {
            @Parameter(name = "itemId", description = "상품 id")
    })
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<CartResponseDTO.cartStockDTO> getCartStock(
            @PathVariable(name = "itemId") Long itemId,
            Authentication authentication
    ) {
        // 비회원인 경우 처리 불가
        if (authentication == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // request에서 member id 추출해 Member 엔티티 찾기
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Cart> cart = cartQueryService.getCartsByItem(itemId, member);

        return ApiResponse.onSuccess(CartConverter.toCartStockDTO(cart));
    }
}
