package com.devminds.rentify.exception;

public class PasswordResetTokenNotFound extends ObjectNotFoundException {
    public PasswordResetTokenNotFound(String message) {
        super(message);
    }

    public PasswordResetTokenNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
