package com.varc.brewnetapp.shared.exception;

// 이메일 인증 중 에러
public class InvalidEmailCodeException extends RuntimeException{
    public InvalidEmailCodeException(String message) {
        super(message);
    }
}