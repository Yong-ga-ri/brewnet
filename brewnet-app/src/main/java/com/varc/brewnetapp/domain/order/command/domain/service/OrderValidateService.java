package com.varc.brewnetapp.domain.order.command.domain.service;

public interface OrderValidateService {
    boolean isOrderFromFranchise(int franchiseCode, int orderCode);

    boolean isOrderDrafted(Integer orderCode);
}
