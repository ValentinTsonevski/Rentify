package com.devminds.rentify.exception;

public class PasswordResetTokenExpired extends RuntimeException {
    public PasswordResetTokenExpired(String message) {
        super(message);
    }

    public PasswordResetTokenExpired(String message, Throwable cause) {
        super(message, cause);
    }
}
