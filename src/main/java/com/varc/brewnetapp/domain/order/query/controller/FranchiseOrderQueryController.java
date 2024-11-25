package com.varc.brewnetapp.domain.order.query.controller;

import com.varc.brewnetapp.common.ResponseMessage;
import com.varc.brewnetapp.domain.member.query.service.MemberServiceImpl;
import com.varc.brewnetapp.domain.order.query.dto.FranchiseOrderDTO;
import com.varc.brewnetapp.domain.order.query.dto.HQOrderDTO;
import com.varc.brewnetapp.domain.order.query.dto.OrderDetailForFranchiseDTO;
import com.varc.brewnetapp.domain.order.query.service.OrderQueryService;
import com.varc.brewnetapp.domain.order.query.service.OrderValidateService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/franchise/orders")
public class FranchiseOrderQueryController {

    private final OrderQueryService orderQueryService;
    private final MemberServiceImpl queryMemberService;

    @Autowired
    public FranchiseOrderQueryController(
            OrderQueryService orderQueryService,
            MemberServiceImpl queryMemberService) {
        this.orderQueryService = orderQueryService;
        this.queryMemberService = queryMemberService;
    }

    @GetMapping("/list")
    @Operation(summary = "가맹점의 주문리스트 조회")
    public ResponseEntity<ResponseMessage<Page<FranchiseOrderDTO>>> getOrderList(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @RequestAttribute("loginId") String loginId,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {
        int franchiseCode = queryMemberService.getFranchiseInfoByLoginId(loginId)
                .getFranchiseCode();

        Page<FranchiseOrderDTO> orderDTOList = orderQueryService.getOrderListForFranchise(
                pageable,
                filter,
                sort,
                startDate,
                endDate,
                franchiseCode
        );
        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", orderDTOList));
    }

    @GetMapping("/detail/{orderCode}")
    @Operation(summary = "가맹점의 주문 상세 정보 조회")
    public ResponseEntity<ResponseMessage<OrderDetailForFranchiseDTO>> getOrderDetail(
            @PathVariable("orderCode") int orderCode,
            @RequestAttribute("loginId") String loginId
    ) {
        OrderDetailForFranchiseDTO orderDetailForFranchiseDTO = orderQueryService.getOrderDetailForFranchiseBy(orderCode, loginId);

        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", orderDetailForFranchiseDTO));
    }
}
