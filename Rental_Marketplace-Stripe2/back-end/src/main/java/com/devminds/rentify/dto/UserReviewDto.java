package com.devminds.rentify.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReviewDto {

    private String firstName;
    private String lastName;
    private int stars;
    private String comment;
    private String profilePicture;

}
