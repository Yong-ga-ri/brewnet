package com.varc.brewnetapp.shared.exception;

public class MandatoryPurchaseNotFound extends RuntimeException {
    public MandatoryPurchaseNotFound(String message) {
        super(message);
    }
}
