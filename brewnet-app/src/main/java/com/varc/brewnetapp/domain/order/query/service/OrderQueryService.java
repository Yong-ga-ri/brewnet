package com.varc.brewnetapp.domain.order.query.service;

import com.varc.brewnetapp.shared.request.Retrieve;
import com.varc.brewnetapp.domain.order.query.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 주문 조회를 위한 Query Service 인터페이스.
 */
public interface OrderQueryService {

    // requested by hq
    Page<HQOrderDTO> getOrderListForHQ(Retrieve retrieve);
    Page<HQOrderDTO> searchOrderListForHQ(Retrieve retrieve);
    OrderRequestDTO printOrderRequest(int orderCode);
    OrderDetailForHQDTO getOrderDetailForHqBy(int orderCode);
    List<HQOrderDTO> getExcelDataForHQBy(Retrieve retrieve);

    // requested by franchise
    Page<FranchiseOrderDTO> getOrderListForFranchise(Retrieve retrieve, int franchiseCode);
    OrderDetailForFranchiseDTO getOrderDetailForFranchiseBy(int orderCode, int franchiseCode);
    Page<FranchiseOrderDTO> searchOrderListForFranchise(Retrieve retrieve, int franchiseCode);
    List<FranchiseOrderDTO> getExcelDataForFranchiseBy(Retrieve retrieve, int franchiseCode);

    // common
    OrderStatusHistory getOrderStatusHistoryByOrderCode(int orderCode);
    List<OrderStatusHistory> getOrderHistoryByOrderCode(int orderCode);
    List<OrderApprovalHistoryDTO> getOrderApprovalHistories(Integer orderCode);
}


