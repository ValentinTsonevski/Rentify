package com.devminds.rentify.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class StripeAdditionalInfoDto {
    private Long userId;

    private LocalDate startDate;
    private LocalDate endDate;
}
