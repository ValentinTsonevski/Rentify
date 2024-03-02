package com.devminds.rentify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class UserRegisterDto {

    @NotEmpty(message = "firstName is required")
    private String firstName;

    @NotEmpty(message = "lastName is required")
    private String lastName;

    @NotEmpty(message = "email is required")
    @Email
    private String email;

    @NotEmpty(message = "password is required")
    @Min(8)
    private String password;

    @NotEmpty(message = "confirm password is required")
    private String confirmPassword;

    @NotEmpty(message = "phoneNumber is required")
    private String phoneNumber;
}
