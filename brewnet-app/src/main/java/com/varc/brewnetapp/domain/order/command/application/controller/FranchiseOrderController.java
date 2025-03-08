package com.varc.brewnetapp.domain.order.command.application.controller;

import com.varc.brewnetapp.domain.auth.query.dto.MemberInfoDTO;
import com.varc.brewnetapp.security.service.RefreshTokenService;
import com.varc.brewnetapp.shared.ResponseMessage;
import com.varc.brewnetapp.domain.member.query.service.MemberService;
import com.varc.brewnetapp.domain.order.command.application.dto.orderrequest.OrderRequestDTO;
import com.varc.brewnetapp.domain.order.command.application.dto.orderrequest.OrderRequestResponseDTO;
import com.varc.brewnetapp.domain.order.command.domain.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/franchise/orders")
public class FranchiseOrderController {

    private final OrderService orderService;
    private final MemberService queryMemberService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public FranchiseOrderController(
            OrderService orderService,
            MemberService queryMemberService,
            RefreshTokenService refreshTokenService
    ) {
        this.orderService = orderService;
        this.queryMemberService = queryMemberService;
        this.refreshTokenService = refreshTokenService;
    }

    // 주문 요청
    @PostMapping
    public ResponseEntity<ResponseMessage<OrderRequestResponseDTO>> franchiseOrder(
            @RequestBody OrderRequestDTO orderRequestDTO,
            @RequestAttribute("loginId") String loginId,
            @RequestAttribute("memberInfoDTO") MemberInfoDTO memberInfoDTO
    ) {
        Integer requestFranchiseCode;
        Integer requestedMemberCode;

        if (memberInfoDTO != null) {
            log.debug("from redis info: {}", memberInfoDTO);
            requestFranchiseCode = memberInfoDTO.getFranchiseCode();
            requestedMemberCode = memberInfoDTO.getMemberCode();
        } else {
            requestFranchiseCode = queryMemberService.getFranchiseInfoByLoginId(loginId).getFranchiseCode();
            requestedMemberCode = queryMemberService.getMemberByLoginId(loginId).getMemberCode();
        }

        OrderRequestResponseDTO orderRequestResponse = orderService.orderRequestByFranchise(orderRequestDTO, requestFranchiseCode, requestedMemberCode);
        return ResponseEntity.ok(
                new ResponseMessage<>(200, "본사로의 주문요청이 완료됐습니다.", orderRequestResponse)
        );
    }

    // 주문 요청 취소
    @DeleteMapping("/{orderCode}")
    public ResponseEntity<ResponseMessage<Void>> cancelOrder(
            @PathVariable(name = "orderCode") Integer orderCode,
            @RequestAttribute(name = "loginId") String loginId
    ) {
        int requestMemberFranchiseCode = queryMemberService.getFranchiseInfoByLoginId(loginId)
                .getFranchiseCode();

        orderService.cancelOrderRequest(orderCode, requestMemberFranchiseCode);
        return ResponseEntity.ok(
                new ResponseMessage<>(204, "주문 요청이 취소되었습니다.", null)
        );
    }
}
