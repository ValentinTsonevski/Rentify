package com.devminds.rentify.exception.advice;

import com.devminds.rentify.exception.ObjectNotFoundException;
import com.devminds.rentify.exception.PasswordResetTokenExpired;
import com.devminds.rentify.exception.UserAccountNotConfirmedException;
import com.devminds.rentify.exception.VerificationTokenExpired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String ERROR_MESSAGE_TEMPLATE = "{ \"error\": \"%s\" }";

    @ExceptionHandler(value = {ObjectNotFoundException.class, PasswordResetTokenExpired.class,
            VerificationTokenExpired.class})
    public ResponseEntity<String> handleNotFound(RuntimeException e) {
        String bodyOfResponse = String.format(ERROR_MESSAGE_TEMPLATE, e.getMessage());
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {UserAccountNotConfirmedException.class})
    public ResponseEntity<String> handleAccountNotConfirmed(RuntimeException e) {
        String bodyOfResponse = String.format(ERROR_MESSAGE_TEMPLATE, e.getMessage());
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.FORBIDDEN);
    }
}
