package com.varc.brewnetapp.shared.exception;

public class EmptyDataException extends RuntimeException{

    public EmptyDataException(String message) {
        super(message);
    }
}
