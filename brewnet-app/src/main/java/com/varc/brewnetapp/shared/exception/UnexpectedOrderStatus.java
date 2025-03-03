package com.varc.brewnetapp.shared.exception;

public class UnexpectedOrderStatus extends RuntimeException {
    public UnexpectedOrderStatus(String message) {
        super(message);
    }
}
