package com.devminds.rentify.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReviewDto {

    @NotEmpty
    private int ratingStars;
    private String comments;
}
