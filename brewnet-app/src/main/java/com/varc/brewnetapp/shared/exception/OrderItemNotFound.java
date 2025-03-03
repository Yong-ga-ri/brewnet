package com.varc.brewnetapp.shared.exception;

public class OrderItemNotFound extends RuntimeException {
    public OrderItemNotFound(String message) {
        super(message);
    }
}
