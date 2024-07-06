package thegoods.server.order.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import thegoods.server.common.enums.OrderStatus;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.presentation.ApiResponse;
import thegoods.server.common.validation.annotation.CheckPage;
import thegoods.server.member.domain.Member;
import thegoods.server.member.exception.handler.MemberHandler;
import thegoods.server.member.implement.query.MemberQueryService;
import thegoods.server.order.converter.OrderConverter;
import thegoods.server.order.domain.OrderItem;
import thegoods.server.order.domain.Orders;
import thegoods.server.order.implement.command.OrderCommandService;
import thegoods.server.order.implement.query.OrderQueryService;
import thegoods.server.order.presentation.dto.OrderRequestDTO;
import thegoods.server.order.presentation.dto.OrderResponseDTO;

import javax.validation.Valid;

import thegoods.server.order.exception.OrderHandler;


@Slf4j
@Tag(name = "Order", description = "주문 관련 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderApi {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;
    private final MemberQueryService memberQueryService;

    @PostMapping
    @Operation(summary = "주문 등록 API", description = "상품에 대한 주문을 등록하는 API 입니다.\n\n" +
            "payType(결제 유형)은 \"ACCOUNT\" 또는 \"CARD\"로 입력해야 합니다.\n\n" +
            "orderDetailDTOList 내부의 JSON에는 itemOptionId(상품 옵션 id)와 amount(주문 수량)을 담아주세요. 해당 상품이 단일 상품이라면 itemOptionId 값은 보내지 않아도 됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<OrderResponseDTO.OrderAddResultDTO> order(@RequestBody @Valid OrderRequestDTO.OrderAddDTO request, Authentication authentication) {
        Member member = null;

        // 비로그인 요청인 경우 비회원 계정 불러오기
        if (authentication == null) {
            member = memberQueryService.findMemberByNickname("no_login_user").orElseThrow(() -> new OrderHandler(ErrorStatus.NO_LOGIN_ORDER_NOT_AVAILABLE));
        } else {
            // request에서 member id 추출해 Member 엔티티 찾기
            member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        }

        Orders orders = orderCommandService.create(request, member);

        return ApiResponse.onSuccess(OrderConverter.toOrderAddResultDto(orders));
    }

    @GetMapping
    @Operation(summary = "나의 주문 목록 조회 API", description = "나의 주문 목록을 조회하는 API 입니다. (구매자 회원용)\n\n" +
            "페이지 번호 (1 이상)과 주문 상태 필터 값을 보내주세요. 주문 상태 필터 값을 보내지 않을 경우, 전체 주문을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameters(value = {
            @Parameter(name = "page", description = "페이지 번호, 1 이상의 숫자를 입력해주세요."),
            @Parameter(name = "status", description = "주문 처리 상태 필터, 선택하지 않으면 전체 주문이 조회됩니다.")

    })
    public ApiResponse<OrderResponseDTO.orderItemPreViewListDTO> myOrderPreview(
            @RequestParam(name = "page") @CheckPage Integer page,
            @RequestParam(name = "status", required = false) OrderStatus orderStatus,
            Authentication authentication
    ) {
        // 비회원인 경우 처리 불가
        if (authentication == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // request에서 member id 추출해 Member 엔티티 찾기
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Page<OrderItem> orderItemList = orderQueryService.getOrderItemList(member, orderStatus, page - 1);

        return ApiResponse.onSuccess(OrderConverter.toOrderPreViewListDTO(orderItemList));
    }

    @PostMapping("/api/nologin/order")
    @Operation(summary = "비회원 주문 조회 API", description = "비회원의 특정 주문 내역을 조회하는 API 입니다.\n\n" +
            "page에는 1이상의 페이지 번호 값을 입력해주세요.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<OrderResponseDTO.orderItemPreViewListDTO> noLoginOrderPreview(@RequestBody @Valid OrderRequestDTO.noLoginOrderViewDTO request) {

        Page<OrderItem> orderItemList = orderQueryService.getNoLoginOrderItemList(request);

        return ApiResponse.onSuccess(OrderConverter.toOrderPreViewListDTO(orderItemList));
    }


    @GetMapping("/{orderItemId}")
    @Operation(summary = "주문 상세 내역 조회 API", description = "주문 상세 내역을 조회하는 API 입니다. (비회원, 구매자 공통)\n\n" +
            "orderItemId(상품 주문 내역 id)을 보내주세요.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameter(name = "orderItemId", description = "주문 상품 내역 id, path variable 입니다.")
    public ApiResponse<OrderResponseDTO.OrderItemViewDTO> orderItemView(
            @PathVariable(name = "orderItemId") Long orderItemId,
            Authentication authentication
    ) {
        Member member = null;

        // 비로그인 요청인 경우 비회원 계정 불러오기
        if (authentication == null) {
            member = memberQueryService.findMemberByNickname("no_login_user").orElseThrow(() -> new OrderHandler(ErrorStatus.NO_LOGIN_ORDER_NOT_AVAILABLE));
        } else {
            // request에서 member id 추출해 Member 엔티티 찾기
            member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        }

        OrderItem orderItem = orderQueryService.getOrderItem(member, orderItemId);

        return ApiResponse.onSuccess(OrderConverter.toOrderItemViewDTO(orderItem));

    }

    @PostMapping("/{orderItemId}/complete")
    @Operation(summary = "주문 구매 확정 API", description = "주문을 구매 확정 처리하는 API 입니다.\n\n" +
            "orderItemId(상품 주문 내역 id)를 입력해주세요.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameter(name = "orderItemId", description = "주문 상품 내역 id, path variable 입니다.")
    public ApiResponse<OrderResponseDTO.OrderItemUpdateResultDTO> orderPurchaseConfirm(
            @PathVariable(name = "orderItemId") Long orderItemId,
            Authentication authentication
    ) {
        // 비회원인 경우 처리 불가
        if (authentication == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // request에서 member id 추출해 Member 엔티티 찾기
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        OrderItem orderItem = orderCommandService.updateStatusToConfirm(orderItemId, member);

        return ApiResponse.onSuccess(OrderConverter.toOrderItemUpdateResultDTO(orderItem));
    }

    @PutMapping("/{orderItemId}/info/address")
    @Operation(summary = "주문 배송지 정보 수정 API", description = "상품 주문 내역의 배송지 정보를 변경하는 API 입니다.\n\n" +
            "orderItemId(상품 주문 내역 id)와 배송지 정보를 보내주세요.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameter(name = "orderItemId", description = "주문 상품 내역 id, path variable 입니다.")
    public ApiResponse<OrderResponseDTO.OrderItemUpdateResultDTO> updateOrderItemAddress(
            @RequestBody OrderRequestDTO.OrderItemAddressUpdateDTO request,
            @PathVariable(name = "orderItemId") Long orderItemId,
            Authentication authentication
    ) {
        Member member = null;

        // 비로그인 요청인 경우 비회원 계정 불러오기
        if (authentication == null) {
            member = memberQueryService.findMemberByNickname("no_login_user").orElseThrow(() -> new OrderHandler(ErrorStatus.NO_LOGIN_ORDER_NOT_AVAILABLE));
        } else {
            // request에서 member id 추출해 Member 엔티티 찾기
            member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        }

        OrderItem orderItem = orderCommandService.updateOrderItemAddress(request, orderItemId, member);

        return ApiResponse.onSuccess(OrderConverter.toOrderItemUpdateResultDTO(orderItem));
    }

    @PutMapping("/{orderItemId}/info/refund")
    @Operation(summary = "주문 환불 계좌 정보 수정 API", description = "상품 주문 내역의 환불 계좌 정보를 변경하는 API 입니다.\n\n" +
            "orderItemId(상품 주문 내역 id)와 환불 계좌 정보를 보내주세요.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameter(name = "orderItemId", description = "주문 상품 내역 id, path variable 입니다.")
    public ApiResponse<OrderResponseDTO.OrderItemUpdateResultDTO> updateOrderItemRefundInfo(
            @RequestBody OrderRequestDTO.OrderItemRefundInfoUpdateDTO request,
            @PathVariable(name = "orderItemId") Long orderItemId,
            Authentication authentication
    ) {
        Member member = null;

        // 비로그인 요청인 경우 비회원 계정 불러오기
        if (authentication == null) {
            member = memberQueryService.findMemberByNickname("no_login_user").orElseThrow(() -> new OrderHandler(ErrorStatus.NO_LOGIN_ORDER_NOT_AVAILABLE));
        } else {
            // request에서 member id 추출해 Member 엔티티 찾기
            member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        }

        OrderItem orderItem = orderCommandService.updateOrderItemRefundInfo(request, orderItemId, member);

        return ApiResponse.onSuccess(OrderConverter.toOrderItemUpdateResultDTO(orderItem));
    }
}
