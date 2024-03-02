package com.devminds.rentify.exception;

public class UserNotFoundException extends ObjectNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
