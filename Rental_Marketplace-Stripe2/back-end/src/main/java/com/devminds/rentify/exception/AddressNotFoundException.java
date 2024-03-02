package com.devminds.rentify.exception;

public class AddressNotFoundException extends ObjectNotFoundException {
    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
