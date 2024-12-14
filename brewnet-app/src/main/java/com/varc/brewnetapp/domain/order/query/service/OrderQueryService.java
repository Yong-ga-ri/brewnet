package com.varc.brewnetapp.domain.order.query.service;

import com.varc.brewnetapp.common.SearchCriteria;
import com.varc.brewnetapp.domain.order.query.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface OrderQueryService {


    // requested by hq
    Page<HQOrderDTO> getOrderListForHQ(
            Pageable pageable,
            String filter,
            String sort,
            String startDate,
            String endDate
    );
    Page<HQOrderDTO> searchOrderListForHQ(
            Pageable pageable,
            String filter,
            String sort,
            String startDate,
            String endDate,
            SearchCriteria criteria,
            String keyword
    );
    OrderRequestDTO printOrderRequest(int orderCode);
    OrderDetailForHQDTO getOrderDetailForHqBy(int orderCode);
    List<HQOrderDTO> getExcelDataForHQBy(
            String startDate,
            String endDate,
            SearchCriteria criteria,
            String keyword
    );


    // requested by franchise
    Page<FranchiseOrderDTO> getOrderListForFranchise(
            Pageable pageable,
            String filter,
            String sort,
            String startDate,
            String endDate,
            int franchiseCode
    );
    OrderDetailForFranchiseDTO getOrderDetailForFranchiseBy(
            int orderCode, String loginId
    );
    Page<FranchiseOrderDTO> searchOrderListForFranchise(
            Pageable pageable,
            String filter,
            String sort,
            String startDate,
            String endDate,
            int franchiseCode,
            SearchCriteria criteria,
            String searchWord
    );
    List<FranchiseOrderDTO> getExcelDataForFranchiseBy(
            String startDate,
            String endDate,
            int franchiseCode,
            SearchCriteria criteria,
            String keyword
    );

    // common
    OrderStatusHistory getOrderStatusHistoryByOrderCode(int orderCode);
    List<OrderStatusHistory> getOrderHistoryByOrderCode(int orderCode);
    List<OrderApprovalHistoryDTO> getOrderApprovalHistories(Integer orderCode);
}
