package com.varc.brewnetapp.domain.order.query.mapper;

import com.varc.brewnetapp.domain.order.query.dto.*;
import com.varc.brewnetapp.shared.request.Retrieve;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    // for HQ
    List<HQOrderDTO> findOrdersForHQBy(@Param("retrieve") Retrieve retrieve);
    OrderDetailForHQDTO findOrderDetailForHqBy(int orderCode);

    // search
    List<HQOrderDTO> searchOrdersForHQByOrderCode(@Param("retrieve") Retrieve retrieve);
    List<HQOrderDTO> searchOrdersForHQByOrderedFranchiseName(@Param("retrieve") Retrieve retrieve);
    List<HQOrderDTO> searchOrdersForHQByOrderManager(@Param("retrieve") Retrieve retrieve);

    // for FRANCHISE
    OrderDetailForFranchiseDTO findOrderDetailForFranchiseBy(int orderCode);
    List<FranchiseOrderDTO> findOrdersForFranchise(@Param("retrieve") Retrieve retrieve,
                                                   @Param("franchiseCode") int franchiseCode);

    // search
    List<FranchiseOrderDTO> searchOrdersForFranchiseByOrderCode(@Param("retrieve") Retrieve retrieve,
                                                                @Param("franchiseCode") int franchiseCode);
    List<FranchiseOrderDTO> searchOrdersForFranchiseByItemName(@Param("retrieve") Retrieve retrieve,
                                                               @Param("franchiseCode") int franchiseCode);

    // for Common
    List<OrderApprovalHistoryDTO> findOrderApprovalHistoriesBy(int orderCode);
    OrderStatusHistory findRecentHistoryByOrderId(@Param("orderCode") int orderCode);
    List<OrderStatusHistory> findOrderHistoriesByOrderId(@Param("orderId") int orderId);
}


