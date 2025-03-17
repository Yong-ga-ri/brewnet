package com.varc.brewnetapp.shared.exception;

public class ApprovalAlreadyCompleted extends RuntimeException {
    public ApprovalAlreadyCompleted(String message) {
        super(message);
    }
}
