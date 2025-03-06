package com.varc.brewnetapp.shared.exception;

public class InvalidOrderApproval extends RuntimeException {
    public InvalidOrderApproval(String message) {
        super(message);
    }
}
