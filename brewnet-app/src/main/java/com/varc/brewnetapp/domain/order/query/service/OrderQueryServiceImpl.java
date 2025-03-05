package com.varc.brewnetapp.domain.order.query.service;

import com.varc.brewnetapp.domain.order.command.domain.service.OrderValidateService;
import com.varc.brewnetapp.shared.request.Retrieve;
import com.varc.brewnetapp.shared.utility.search.SearchCriteria;
import com.varc.brewnetapp.domain.member.query.service.MemberService;
import com.varc.brewnetapp.domain.order.query.dto.*;
import com.varc.brewnetapp.domain.order.query.mapper.OrderCounterMapper;
import com.varc.brewnetapp.domain.order.query.mapper.OrderMapper;
import com.varc.brewnetapp.shared.exception.InvalidCriteriaException;
import com.varc.brewnetapp.shared.exception.NoAccessAuthoritiesException;
import com.varc.brewnetapp.shared.exception.OrderNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class OrderQueryServiceImpl implements OrderQueryService {
    private final OrderCounterMapper orderCounterMapper;
    private final OrderMapper orderMapper;
    private final OrderValidateService orderValidateService;
    private final MemberService memberService;

    @Autowired
    public OrderQueryServiceImpl(
            OrderCounterMapper orderCounterMapper,
            OrderMapper orderMapper,
            OrderValidateService orderValidateService,
            MemberService memberService
    ) {
        this.orderCounterMapper = orderCounterMapper;
        this.orderMapper = orderMapper;
        this.orderValidateService = orderValidateService;
        this.memberService = memberService;
    }

    // for HQ
    @Override
    @Transactional
    public Page<HQOrderDTO> getOrderListForHQ(Retrieve retrieve) {

        // TODO: check if filter value is one of ["UNCONFIRMED", null]
        // TODO: check if sort value is one of ["createdAtDesc", "createdAtAsc", "sumPriceDesc", "sumPriceAsc"]

        return new PageImpl<>(
                orderMapper.findOrdersForHQBy(retrieve),
                retrieve.getPageable(),
                orderCounterMapper.countOrdersForHq(retrieve)
        );
    }

    @Override
    @Transactional
    public Page<HQOrderDTO> searchOrderListForHQ(Retrieve retrieve) {

        List<HQOrderDTO> orderList;
        int totalCount;

        switch (retrieve.getCriteria()) {
            case ORDER_CODE -> {
                orderList = orderMapper.searchOrdersForHQByOrderCode(retrieve);
                totalCount = orderCounterMapper.countSearchedOrdersForHQByOrderCode(retrieve);
            }
            case ORDERED_FRANCHISE_NAME -> {
                orderList = orderMapper.searchOrdersForHQByOrderedFranchiseName(retrieve);
                totalCount = orderCounterMapper.countSearchedOrdersForHQByOrderedFranchiseName(retrieve);
            }
            case ORDER_MANAGER -> {
                orderList = orderMapper.searchOrdersForHQByOrderManager(retrieve);
                totalCount = orderCounterMapper.countSearchedOrdersForHQByOrderManager(retrieve);
            }
            case ALL -> {
                orderList = orderMapper.findOrdersForHQBy(retrieve);
                totalCount = orderCounterMapper.countOrdersForHq(retrieve);
            }
            default -> throw new InvalidCriteriaException();
        }
        return new PageImpl<>(orderList, retrieve.getPageable(), totalCount);
    }

    @Override
    @Transactional
    public List<HQOrderDTO> getExcelDataForHQBy(Retrieve retrieve) {
        List<HQOrderDTO> hqOrderDTOList;
        switch (retrieve.getCriteria()) {
            case ORDER_CODE -> hqOrderDTOList = orderMapper.searchOrdersForHQByOrderCode(retrieve);
            case ORDERED_FRANCHISE_NAME -> hqOrderDTOList = orderMapper.searchOrdersForHQByOrderedFranchiseName(retrieve);
            case ORDER_MANAGER -> hqOrderDTOList = orderMapper.searchOrdersForHQByOrderManager(retrieve);
            case ALL -> hqOrderDTOList = orderMapper.findOrdersForHQBy(retrieve);
            default -> throw new InvalidCriteriaException();
        }
        return hqOrderDTOList;
    }

    @Override
    @Transactional
    public OrderDetailForHQDTO getOrderDetailForHqBy(int orderCode) {
        OrderDetailForHQDTO orderDetail = orderMapper.findOrderDetailForHqBy(orderCode);
        if (orderDetail == null) {
            throw new OrderNotFound("Order not found");
        } else {
            return orderDetail;
        }
    }

    @Override
    public OrderRequestDTO printOrderRequest(int orderCode) {

        // TODO: check is tbl_order.approval_status is 'APPROVED'
        // TODO: get order detail information

        return null;
    }

    // for franchise
    @Override
    @Transactional
    public Page<FranchiseOrderDTO> getOrderListForFranchise(
            Retrieve retrieve,
            int franchiseCode
    ) {

        // TODO: check if franchise valid
        // TODO: get order list query for franchise

        return new PageImpl<>(
                orderMapper.findOrdersForFranchise(retrieve, franchiseCode),
                retrieve.getPageable(),
                orderCounterMapper.countOrdersForFranchise(retrieve, franchiseCode)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FranchiseOrderDTO> searchOrderListForFranchise(Retrieve retrieve, int franchiseCode) {

        List<FranchiseOrderDTO> orderList;
        int totalCount;

        switch (retrieve.getCriteria()) {
            case ORDER_CODE -> {
                orderList = orderMapper.searchOrdersForFranchiseByOrderCode(retrieve, franchiseCode);
                totalCount = orderCounterMapper.countSearchedOrdersForFranchiseByOrderCode(retrieve, franchiseCode);
            }
            case ITEM_NAME -> {
                orderList = orderMapper.searchOrdersForFranchiseByItemName(retrieve, franchiseCode);
                totalCount = orderCounterMapper.countSearchedOrdersForFranchiseByItemName(retrieve, franchiseCode);
            }
            case ALL -> {
                orderList = orderMapper.findOrdersForFranchise(retrieve, franchiseCode);
                totalCount = orderCounterMapper.countOrdersForFranchise(retrieve, franchiseCode);
            }
            default -> throw new InvalidCriteriaException();
        }
        return new PageImpl<>(orderList, retrieve.getPageable(), totalCount);
    }

    @Override
    public List<FranchiseOrderDTO> getExcelDataForFranchiseBy(
            Retrieve retrieve,
            int franchiseCode
    ) {
        List<FranchiseOrderDTO> franchiseOrderDTOList;
        switch (retrieve.getCriteria()) {
            case ORDER_CODE -> franchiseOrderDTOList = orderMapper.searchOrdersForFranchiseByOrderCode(retrieve, franchiseCode);
            case ITEM_NAME -> franchiseOrderDTOList = orderMapper.searchOrdersForFranchiseByItemName(retrieve, franchiseCode);
            case ALL -> franchiseOrderDTOList = orderMapper.findOrdersForFranchise(retrieve, franchiseCode);
            default -> throw new InvalidCriteriaException();
        }
        return franchiseOrderDTOList;
    }

    @Override
    @Transactional
    public OrderDetailForFranchiseDTO getOrderDetailForFranchiseBy(int orderCode, String loginId) {
        int franchiseCode = getFranchiseCodeByLoginId(loginId);

        boolean isOrderFromFranchise = orderValidateService.isOrderFromFranchise(
                franchiseCode,
                orderCode
        );

        if (isOrderFromFranchise) {
            return orderMapper.findOrderDetailForFranchiseBy(orderCode);
        } else {
            throw new NoAccessAuthoritiesException("No Authorization for order " + orderCode + ", franchiseCode: " + franchiseCode);
        }
    }


    // for common
    // 해당 주문의 모든 상태 변경 내역 조회
    @Override
    @Transactional
    public List<OrderStatusHistory> getOrderHistoryByOrderCode(int orderId) {
        return orderMapper.findOrderHistoriesByOrderId(orderId);
    }

    // 해당 주문의 최신 상태 조회
    @Override
    @Transactional
    public OrderStatusHistory getOrderStatusHistoryByOrderCode(int orderCode) {
        return orderMapper.findRecentHistoryByOrderId(orderCode);
    }

    // 해당 주문의 결재 히스토리 조회
    @Override
    public List<OrderApprovalHistoryDTO> getOrderApprovalHistories(Integer orderCode) {
        return orderMapper.findOrderApprovalHistoriesBy(orderCode);
    }

    @Transactional
    public int getFranchiseCodeByLoginId(String loginId) {
        return memberService.getFranchiseInfoByLoginId(loginId).getFranchiseCode();
    }
}
