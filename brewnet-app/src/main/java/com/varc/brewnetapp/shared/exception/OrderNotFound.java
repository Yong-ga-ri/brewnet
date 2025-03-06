package com.varc.brewnetapp.shared.exception;

public class OrderNotFound extends RuntimeException {
    public OrderNotFound() {
        super("Order Not Found");
    }

    public OrderNotFound(String message) {
        super(message);
    }
}
