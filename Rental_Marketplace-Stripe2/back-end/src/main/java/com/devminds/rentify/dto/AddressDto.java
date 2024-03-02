package com.devminds.rentify.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddressDto {

    @NotEmpty(message = "city is required")
    private String city;

    @NotEmpty(message = "street is required")
    private String street;

    @NotEmpty(message = "postCode is required")
    private String postCode;

    @NotEmpty(message = "streetNumber is required")
    private String streetNumber;
}
