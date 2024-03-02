package com.devminds.rentify.controller;

import com.devminds.rentify.dto.StripeAdditionalInfoDto;
import com.devminds.rentify.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StripeController {
    private final StripeService stripeService;

    @PostMapping("/rentify/stripe/checkout/{id}")
    public ResponseEntity<String> createCheckout(@PathVariable Long id, @RequestBody StripeAdditionalInfoDto stripeAdditionalInfoDto) throws StripeException {
        String session = stripeService.createCheckoutSession(id,stripeAdditionalInfoDto);

        return new ResponseEntity<>(session,HttpStatus.OK);
    }

    @PostMapping("/rentify/stripe/checkout/webhook")
    public ResponseEntity<String> checkoutHook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws SignatureVerificationException {

            stripeService.hook(payload, sigHeader);

            return new ResponseEntity<>(HttpStatus.OK);
    }


}
