package com.varc.brewnetapp.domain.order.command.application.service;

import com.varc.brewnetapp.domain.order.command.domain.repository.OrderApprovalRepository;
import com.varc.brewnetapp.domain.order.command.domain.repository.OrderRepository;
import com.varc.brewnetapp.domain.order.command.domain.service.OrderValidateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderValidateServiceImpl implements OrderValidateService {

    private final OrderRepository orderRepository;
    private final OrderApprovalRepository orderApprovalRepository;

    @Autowired
    public OrderValidateServiceImpl(OrderRepository orderRepository, OrderApprovalRepository orderApprovalRepository) {
        this.orderRepository = orderRepository;
        this.orderApprovalRepository = orderApprovalRepository;
    }

    @Transactional
    @Override
    public boolean isOrderFromFranchise(int franchiseCode, int orderCode) {
        return orderRepository.checkIsOrderFrom(franchiseCode, orderCode);
    }

    @Transactional
    @Override
    public boolean isOrderDrafted(int orderCode) {
        return orderApprovalRepository.checkIsOrderDrafted(orderCode);
    }
}
