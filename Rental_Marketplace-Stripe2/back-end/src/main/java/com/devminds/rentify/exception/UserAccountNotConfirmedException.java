package com.devminds.rentify.exception;

public class UserAccountNotConfirmedException extends RuntimeException {
    public UserAccountNotConfirmedException(String message) {
        super(message);
    }

    public UserAccountNotConfirmedException(String message, Throwable cause) {
        super(message, cause);
    }
}
