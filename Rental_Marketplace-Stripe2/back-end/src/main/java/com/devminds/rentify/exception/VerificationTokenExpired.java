package com.devminds.rentify.exception;

public class VerificationTokenExpired extends RuntimeException {
    public VerificationTokenExpired(String message) {
        super(message);
    }

    public VerificationTokenExpired(String message, Throwable cause) {
        super(message, cause);
    }
}
