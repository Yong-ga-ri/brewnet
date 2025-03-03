package com.varc.brewnetapp.shared.exception;

public class NoAccessAuthoritiesException extends RuntimeException {
    public NoAccessAuthoritiesException(String message) {
        super(message);
    }
}
