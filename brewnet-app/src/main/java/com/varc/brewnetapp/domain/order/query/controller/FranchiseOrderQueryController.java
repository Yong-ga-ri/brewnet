package com.varc.brewnetapp.domain.order.query.controller;

import com.varc.brewnetapp.domain.auth.query.dto.MemberInfoDTO;
import com.varc.brewnetapp.shared.ResponseMessage;
import com.varc.brewnetapp.shared.request.Retrieve;
import com.varc.brewnetapp.shared.utility.search.SearchCriteria;
import com.varc.brewnetapp.domain.order.query.dto.FranchiseOrderDTO;
import com.varc.brewnetapp.domain.order.query.dto.OrderDetailForFranchiseDTO;
import com.varc.brewnetapp.domain.order.query.service.OrderQueryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.varc.brewnetapp.shared.interceptor.CachingMemberInfoInterceptor.SERVLET_ATTRIBUTE_MEMBER_INFO_KEY;

@Slf4j
@RestController
@RequestMapping("api/v1/franchise/orders")
public class FranchiseOrderQueryController {

    private final OrderQueryService orderQueryService;

    @Autowired
    public FranchiseOrderQueryController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @GetMapping("/list")
    @Operation(summary = "가맹점의 주문리스트 조회")
    public ResponseEntity<ResponseMessage<Page<FranchiseOrderDTO>>> getOrderList(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @RequestAttribute(SERVLET_ATTRIBUTE_MEMBER_INFO_KEY) MemberInfoDTO memberInfoDTO,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {

        return ResponseEntity.ok(new ResponseMessage<>(
                200,
                "OK",
                orderQueryService.getOrderListForFranchise(
                        Retrieve.with(pageable, filter, sort, startDate, endDate, null, null),
                        memberInfoDTO.getFranchiseCode()
                )
        ));
    }


    @GetMapping("/search")
    @Operation(summary = "가맹점의 주문리스트 검색")
    public ResponseEntity<ResponseMessage<Page<FranchiseOrderDTO>>> searchOrderList(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @RequestAttribute(SERVLET_ATTRIBUTE_MEMBER_INFO_KEY) MemberInfoDTO memberInfoDTO,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "criteria", required = false) SearchCriteria criteria,
            @RequestParam(name = "searchWord", required = false) String searchWord
    ) {

        return ResponseEntity.ok(new ResponseMessage<>(
                200,
                "OK",
                orderQueryService.searchOrderListForFranchise(
                        Retrieve.with(pageable, filter, sort, startDate, endDate, criteria, searchWord),
                        memberInfoDTO.getFranchiseCode()
                )
        ));
    }

    @GetMapping("/excel")
    public ResponseEntity<ResponseMessage<List<FranchiseOrderDTO>>> getOrderDetailList(
            @RequestAttribute(SERVLET_ATTRIBUTE_MEMBER_INFO_KEY) MemberInfoDTO memberInfoDTO,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "criteria", required = false) SearchCriteria criteria,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {

        return ResponseEntity.ok(new ResponseMessage<>(
                200,
                "OK",
                orderQueryService.getExcelDataForFranchiseBy(
                        Retrieve.with(null, null, null, startDate, endDate, criteria, keyword),
                        memberInfoDTO.getFranchiseCode())
                ));

    }

    @GetMapping("/detail/{orderCode}")
    @Operation(summary = "가맹점의 주문 상세 정보 조회")
    public ResponseEntity<ResponseMessage<OrderDetailForFranchiseDTO>> getOrderDetail(
            @PathVariable("orderCode") int orderCode,
            @RequestAttribute(SERVLET_ATTRIBUTE_MEMBER_INFO_KEY) MemberInfoDTO memberInfoDTO
    ) {

        return ResponseEntity.ok(new ResponseMessage<>(
                200,
                "OK",
                orderQueryService.getOrderDetailForFranchiseBy(orderCode, memberInfoDTO.getFranchiseCode())
        ));
    }
}
