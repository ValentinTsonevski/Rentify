package com.devminds.rentify.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RentDto {
    private long id;

    @NotNull
    private PlainItemDto item;
    @NotNull
    private PlainUserDto user;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
}