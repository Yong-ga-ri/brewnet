package com.varc.brewnetapp.shared.exception;

public class OrderDraftAlreadyApproved extends RuntimeException {
    public OrderDraftAlreadyApproved(String message) {
        super(message);
    }
}
