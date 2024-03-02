package com.devminds.rentify.dto;

import lombok.Data;

@Data
public class LikedItemDto {
    private Long id;

    private PlainItemDto item;

    private PlainUserDto user;
}
