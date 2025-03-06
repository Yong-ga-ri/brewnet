package com.varc.brewnetapp.shared.exception;

public class InvalidOrderItems extends RuntimeException {
    public InvalidOrderItems(String message) {
        super(message);
    }
}
