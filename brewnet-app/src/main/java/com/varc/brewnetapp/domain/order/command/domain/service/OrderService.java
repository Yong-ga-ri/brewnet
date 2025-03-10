package com.varc.brewnetapp.domain.order.command.domain.service;

import com.varc.brewnetapp.domain.order.command.application.dto.DrafterRejectOrderRequestDTO;
import com.varc.brewnetapp.domain.order.command.application.dto.OrderApproveRequestDTO;
import com.varc.brewnetapp.domain.order.command.application.dto.OrderRequestApproveDTO;
import com.varc.brewnetapp.domain.order.command.application.dto.OrderApprovalRequestRejectDTO;
import com.varc.brewnetapp.domain.order.command.application.dto.orderrequest.OrderItemDTO;
import com.varc.brewnetapp.domain.order.command.application.dto.orderrequest.OrderRequestDTO;
import com.varc.brewnetapp.domain.order.command.application.dto.orderrequest.OrderRequestResponseDTO;

import java.util.List;

public interface OrderService {

    // 가맹점의 주문요청
    OrderRequestResponseDTO orderRequestByFranchise(
            OrderRequestDTO orderRequestRequestDTO,
            int requestFranchiseCode,
            int orderRequestedMember
    );

    void addItemsPerOrder(int orderCode,
                          List<OrderItemDTO> orderRequestRequestDTO);

    void cancelOrderRequest(Integer orderCode, Integer franchiseCode);

    void rejectOrderByDrafter(int orderCode,
                              DrafterRejectOrderRequestDTO drafterRejectOrderRequestDTO,
                              String loginId);

    // 주문 결재 상신
    void requestApproveOrder(int orderCode,
                                int memberCode,
                                OrderApproveRequestDTO orderApproveRequestDTO);

    // 주문 결재 상신 취소
    void cancelOrderApproval(int orderCode, int memberCode);

    // 상신된 주문에 대한 승인
    void approveOrderDraft(int orderCode, int memberCode, OrderRequestApproveDTO orderRequestApproveDTO);

    // 상신된 주문에 대한 반려
    void rejectOrderDraft(int orderCode, int memberCode, OrderApprovalRequestRejectDTO orderApprovalRequestRejectDTO);

}
