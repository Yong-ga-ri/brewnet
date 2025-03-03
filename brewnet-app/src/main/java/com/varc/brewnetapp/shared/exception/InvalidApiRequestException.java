package com.varc.brewnetapp.shared.exception;

public class InvalidApiRequestException extends RuntimeException {
    public InvalidApiRequestException(String message) {
        super(message);
    }
}
