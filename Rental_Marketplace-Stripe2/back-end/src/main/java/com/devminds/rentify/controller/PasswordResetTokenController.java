package com.devminds.rentify.controller;

import com.devminds.rentify.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentify/password")
public class PasswordResetTokenController {

    private static final String PASSWORD_RESET_EMAIL_SENT_MESSAGE = "Password reset email sent successfully";
    private static final String PASSWORD_RESET_SUCCESS_MESSAGE = "Password reset successfully";
    private final PasswordResetTokenService passwordResetTokenService;

    @Autowired
    public PasswordResetTokenController(PasswordResetTokenService passwordResetTokenService) {
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email) {
        passwordResetTokenService.resetPassword(email);
        return ResponseEntity.ok(PASSWORD_RESET_EMAIL_SENT_MESSAGE);
    }

    @PostMapping("/reset-confirm")
    public ResponseEntity<String> resetPasswordConfirm(@RequestParam("token") String token,
                                                       @RequestBody String password) {
        passwordResetTokenService.resetPasswordConfirm(token, password);
        return ResponseEntity.ok(PASSWORD_RESET_SUCCESS_MESSAGE);
    }

}
