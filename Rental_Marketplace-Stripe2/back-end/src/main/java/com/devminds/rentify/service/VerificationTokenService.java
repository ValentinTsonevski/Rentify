package com.devminds.rentify.service;

import com.devminds.rentify.entity.User;
import com.devminds.rentify.entity.VerificationToken;
import com.devminds.rentify.exception.UserNotFoundException;
import com.devminds.rentify.exception.VerificationTokenExpired;
import com.devminds.rentify.exception.VerificationTokenNotFound;
import com.devminds.rentify.repository.UserRepository;
import com.devminds.rentify.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenService {
    private static final String USER_EMAIL_NOT_FOUND_MESSAGE = "User with email %s not found.";
    private static final String MESSAGE_SUBJECT = "Account Verification";
    private static final String EMAIL_BODY = "Click the following link to verify your account: ";
    private static final String ACCOUNT_VERIFICATION_LINK = "http://localhost:3000/account-verification?token=";
    private static final int ACCOUNT_VERIFICATION_TOKEN_EXPIRATION_DAYS = 1;
    private static final String TOKEN_NOT_FOUND_MESSAGE = "Token not found.";
    private static final String TOKEN_EXPIRED_MESSAGE = "Token has already expired!";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenService(EmailService emailService, UserRepository userRepository,
                                    VerificationTokenRepository verificationTokenRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public void sendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MESSAGE, email)));

        String verificationTokenValue = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(verificationTokenValue);
        verificationToken.setExpirationDateTime(LocalDateTime.now().plusDays(ACCOUNT_VERIFICATION_TOKEN_EXPIRATION_DAYS));
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);

        String resetLink = ACCOUNT_VERIFICATION_LINK + verificationToken.getToken();
        String emailBody = EMAIL_BODY + resetLink;
        emailService.sendEmail(email, MESSAGE_SUBJECT, emailBody);
    }

    public void confirmAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new VerificationTokenNotFound(TOKEN_NOT_FOUND_MESSAGE));

        if (verificationToken.getExpirationDateTime().isBefore(LocalDateTime.now())) {
            throw new VerificationTokenExpired(TOKEN_EXPIRED_MESSAGE);
        }

        User user = verificationToken.getUser();
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        }

        user.setVerified(true);
        userRepository.saveAndFlush(user);
    }
}
