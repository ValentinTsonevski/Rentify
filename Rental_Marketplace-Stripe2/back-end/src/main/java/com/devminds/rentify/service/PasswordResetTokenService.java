package com.devminds.rentify.service;

import com.devminds.rentify.entity.PasswordResetToken;
import com.devminds.rentify.entity.User;
import com.devminds.rentify.exception.PasswordResetTokenExpired;
import com.devminds.rentify.exception.PasswordResetTokenNotFound;
import com.devminds.rentify.exception.UserNotFoundException;
import com.devminds.rentify.repository.PasswordResetTokenRepository;
import com.devminds.rentify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetTokenService {
    private static final String USER_EMAIL_NOT_FOUND_MESSAGE = "User with email %s not found.";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";
    private static final String TOKEN_EXPIRED_MESSAGE = "Token has already expired!";
    private static final String TOKEN_NOT_FOUND_MESSAGE = "Token not found.";
    private static final String RESET_PASSWORD_LINK = "http://localhost:3000/reset-password?token=";
    private static final String MESSAGE_SUBJECT = "Password Reset";
    private static final String EMAIL_BODY = "Click the following link to reset your password: ";
    private static final int RESET_PASSWORD_TOKEN_EXPIRATION_MINUTES = 5;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetTokenService(UserRepository userRepository, EmailService emailService,
                                     PasswordResetTokenRepository passwordResetTokenRepository,
                                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MESSAGE, email)));

        String resetTokenValue = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(resetTokenValue);
        resetToken.setExpirationDateTime(LocalDateTime.now().plusMinutes(RESET_PASSWORD_TOKEN_EXPIRATION_MINUTES));
        resetToken.setUser(user);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = RESET_PASSWORD_LINK + resetToken.getToken();
        String emailBody = EMAIL_BODY + resetLink;
        emailService.sendEmail(email, MESSAGE_SUBJECT, emailBody);
    }

    public void resetPasswordConfirm(String token, String password) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new PasswordResetTokenNotFound(TOKEN_NOT_FOUND_MESSAGE));

        if (resetToken.getExpirationDateTime().isBefore(LocalDateTime.now())) {
            throw new PasswordResetTokenExpired(TOKEN_EXPIRED_MESSAGE);
        }

        User user = resetToken.getUser();

        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.saveAndFlush(user);
    }
}
