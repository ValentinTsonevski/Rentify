package com.devminds.rentify.exception;

public class VerificationTokenNotFound extends ObjectNotFoundException {
    public VerificationTokenNotFound(String message) {
        super(message);
    }

    public VerificationTokenNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
