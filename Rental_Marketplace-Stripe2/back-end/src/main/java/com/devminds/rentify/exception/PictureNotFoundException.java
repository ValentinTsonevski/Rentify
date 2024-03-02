package com.devminds.rentify.exception;

public class PictureNotFoundException extends ObjectNotFoundException {
    public PictureNotFoundException(String message) {
        super(message);
    }

    public PictureNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
