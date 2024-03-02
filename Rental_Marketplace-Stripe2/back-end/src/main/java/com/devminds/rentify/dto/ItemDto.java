package com.devminds.rentify.dto;

import com.devminds.rentify.entity.Address;
import com.devminds.rentify.entity.Category;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ItemDto {
    private Long id;

    @NotEmpty
    @Size(max = 100)
    private String name;

    @Size(max = 1024)
    private String description;

    @NotEmpty
    @Positive
    private BigDecimal price;

    @NotEmpty
    private LocalDateTime postedDate;

    @Positive
    @NotEmpty
    private float deposit;

    private Category category;

    private String thumbnail;

    private Boolean isActive;

    @NotNull
    private PlainUserDto user;

    private Address address;
}
