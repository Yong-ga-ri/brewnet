package com.varc.brewnetapp.shared.exception;

public class MustBuyItemAlreadySet extends RuntimeException {
    public MustBuyItemAlreadySet(String message) {
        super(message);
    }
}
