package com.varc.brewnetapp.shared.domain.order;



public enum Available {
    AVAILABLE("AVAILABLE")
    , UNAVAILABLE("UNAVAILABLE")
    ;

    private final String value;

    Available(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
