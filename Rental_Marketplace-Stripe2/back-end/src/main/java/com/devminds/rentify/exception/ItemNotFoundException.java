package com.devminds.rentify.exception;

public class ItemNotFoundException extends ObjectNotFoundException {
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
