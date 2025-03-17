package com.varc.brewnetapp.shared.exception;

public class InvalidStockException extends RuntimeException{
    public InvalidStockException() {
        super("재고가 존재하지 않습니다.");
    }
    public InvalidStockException(String message) {
        super(message);
    }
}
