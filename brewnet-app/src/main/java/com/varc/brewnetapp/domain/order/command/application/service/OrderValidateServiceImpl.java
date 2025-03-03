package com.varc.brewnetapp.domain.order.command.application.service;

import com.varc.brewnetapp.domain.order.command.domain.repository.OrderRepository;
import com.varc.brewnetapp.domain.order.command.domain.service.OrderValidateService;
import com.varc.brewnetapp.domain.order.query.mapper.OrderValidateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderValidateServiceImpl implements OrderValidateService {

    private final OrderValidateMapper orderValidateMapper;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderValidateServiceImpl(OrderValidateMapper orderValidateMapper, OrderRepository orderRepository) {
        this.orderValidateMapper = orderValidateMapper;
        this.orderRepository = orderRepository;
    }

    @Transactional
    @Override
    public boolean isOrderFromFranchise(int franchiseCode, int orderCode) {
        return orderRepository.checkIsOrderFrom(franchiseCode, orderCode);
    }

    @Override
    public boolean isOrderDrafted(Integer orderCode) {
        return orderValidateMapper.checkIsOrderDrafted(orderCode);
    }
}
