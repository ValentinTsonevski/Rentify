package com.devminds.rentify.exception;

public class EmailNotFoundException extends ObjectNotFoundException {
    public EmailNotFoundException(String message) {
        super(message);
    }

    public EmailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
