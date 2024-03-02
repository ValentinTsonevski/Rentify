package com.devminds.rentify.auth;

import com.devminds.rentify.config.JwtService;
import com.devminds.rentify.config.UserMapper;
import com.devminds.rentify.dto.LoginDto;
import com.devminds.rentify.dto.UserRegisterDto;
import com.devminds.rentify.entity.User;
import com.devminds.rentify.exception.UserAccountNotConfirmedException;
import com.devminds.rentify.exception.UserNotFoundException;
import com.devminds.rentify.repository.RoleRepository;
import com.devminds.rentify.service.StripeService;
import com.devminds.rentify.service.UserService;
import com.stripe.exception.StripeException;
import com.devminds.rentify.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthService {
    private static final String ACCOUNT_NOT_CONFIRMED_MESSAGE = "Account has not been confirmed yet.";

    @Value("${active-profile}")
    private String defaultPicture;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final VerificationTokenService verificationTokenService;

    @Override
    public AuthenticationResponse register(UserRegisterDto userRegisterDto) {
        User user = userMapper.mapToUser(userRegisterDto);
        user.setRole(roleRepository.findUserRole());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setProfilePicture(defaultPicture);
        user.setVerified(false);
        userService.saveUser(user);
        verificationTokenService.sendVerificationEmail(user.getEmail());

        return AuthenticationResponse.builder()
                .email(user.getEmail()).build();
    }

    @Override
    public AuthenticationResponse login(LoginDto loginDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        var user = userService.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isVerified()) {
            throw new UserAccountNotConfirmedException(ACCOUNT_NOT_CONFIRMED_MESSAGE);
        }

        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}
