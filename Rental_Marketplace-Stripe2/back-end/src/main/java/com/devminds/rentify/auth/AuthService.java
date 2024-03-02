package com.devminds.rentify.auth;

import com.devminds.rentify.dto.LoginDto;
import com.devminds.rentify.dto.UserRegisterDto;
import com.stripe.exception.StripeException;

import java.io.IOException;

public interface AuthService {
    AuthenticationResponse register(UserRegisterDto userRegisterDto) throws IOException, StripeException;

    AuthenticationResponse login(LoginDto loginDto);
}
