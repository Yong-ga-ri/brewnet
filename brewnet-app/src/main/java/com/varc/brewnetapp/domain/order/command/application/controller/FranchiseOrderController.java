package com.varc.brewnetapp.domain.order.command.application.controller;

import com.varc.brewnetapp.domain.auth.query.dto.MemberInfoDTO;
import com.varc.brewnetapp.shared.ResponseMessage;
import com.varc.brewnetapp.domain.order.command.application.dto.orderrequest.OrderRequestDTO;
import com.varc.brewnetapp.domain.order.command.application.dto.orderrequest.OrderRequestResponseDTO;
import com.varc.brewnetapp.domain.order.command.domain.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.varc.brewnetapp.shared.interceptor.CachingMemberInfoInterceptor.SERVLET_ATTRIBUTE_MEMBER_INFO_KEY;

@Slf4j
@RestController
@RequestMapping("api/v1/franchise/orders")
public class FranchiseOrderController {

    private final OrderService orderService;

    @Autowired
    public FranchiseOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 주문 요청
    @PostMapping
    public ResponseEntity<ResponseMessage<OrderRequestResponseDTO>> franchiseOrder(
            @RequestBody OrderRequestDTO orderRequestDTO,
            @RequestAttribute(SERVLET_ATTRIBUTE_MEMBER_INFO_KEY) MemberInfoDTO memberInfoDTO
    ) {

        return ResponseEntity.ok(
                new ResponseMessage<>(
                        200,
                        "본사로의 주문요청이 완료됐습니다.",
                        orderService.orderRequestByFranchise(orderRequestDTO,
                                memberInfoDTO.getFranchiseCode(),
                                memberInfoDTO.getMemberCode()
                        )
                )
        );
    }

    // 주문 요청 취소
    @DeleteMapping("/{orderCode}")
    public ResponseEntity<ResponseMessage<Void>> cancelOrder(
            @PathVariable(name = "orderCode") Integer orderCode,
            @RequestAttribute(SERVLET_ATTRIBUTE_MEMBER_INFO_KEY) MemberInfoDTO memberInfoDTO
    ) {
        orderService.cancelOrderRequest(orderCode, memberInfoDTO.getFranchiseCode());
        return ResponseEntity.ok(
                new ResponseMessage<>(204, "주문 요청이 취소되었습니다.", null)
        );
    }
}
