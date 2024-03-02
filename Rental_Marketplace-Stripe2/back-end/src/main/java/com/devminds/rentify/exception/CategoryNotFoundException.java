package com.devminds.rentify.exception;

public class CategoryNotFoundException extends ObjectNotFoundException {
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
