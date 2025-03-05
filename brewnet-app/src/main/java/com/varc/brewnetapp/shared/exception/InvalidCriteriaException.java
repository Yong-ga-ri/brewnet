package com.varc.brewnetapp.shared.exception;

public class InvalidCriteriaException extends RuntimeException {
    public InvalidCriteriaException() {
        super("Invalid Order Criteria.");
    }
    public InvalidCriteriaException(String message) {
        super(message);
    }
}
