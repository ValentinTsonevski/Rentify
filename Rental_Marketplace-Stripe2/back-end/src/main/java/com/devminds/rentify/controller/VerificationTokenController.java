package com.devminds.rentify.controller;

import com.devminds.rentify.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentify/verification")
public class VerificationTokenController {
    private static final String CONFIRMATION_LINK_SENT_MESSAGE = "Confirmation link sent successfully";
    private static final String ACCOUNT_CONFIRMED_MESSAGE = "Account successfully confirmed.";
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public VerificationTokenController(VerificationTokenService verificationTokenService) {
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping
    public ResponseEntity<String> sendVerificationToken(@RequestParam("email") String email) {
        verificationTokenService.sendVerificationEmail(email);
        return ResponseEntity.ok(CONFIRMATION_LINK_SENT_MESSAGE);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmAccount(@RequestParam("token") String token) {
        verificationTokenService.confirmAccount(token);
        return ResponseEntity.ok(ACCOUNT_CONFIRMED_MESSAGE);
    }
}
