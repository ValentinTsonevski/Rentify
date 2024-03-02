package com.devminds.rentify.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryDto {
    private Long id;
    private PlainUserDto user;
    private PlainItemDto item;
    private LocalDateTime date;
}
