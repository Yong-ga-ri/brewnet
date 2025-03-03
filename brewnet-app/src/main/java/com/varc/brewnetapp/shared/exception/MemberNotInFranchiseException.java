package com.varc.brewnetapp.shared.exception;

public class MemberNotInFranchiseException extends RuntimeException {
    public MemberNotInFranchiseException(String message) {
        super(message);
    }
}
