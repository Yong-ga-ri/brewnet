package com.varc.brewnetapp.shared.exception;

public class OrderApprovalAlreadyExist extends RuntimeException {
    public OrderApprovalAlreadyExist(String message) {
        super(message);
    }
}
