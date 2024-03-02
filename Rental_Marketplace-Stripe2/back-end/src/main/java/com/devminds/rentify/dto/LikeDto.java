package com.devminds.rentify.dto;

import lombok.Data;

@Data
public class LikeDto {

    private long itemId;
    private long userId;
    private boolean isLiked;

    public LikeDto(long itemId, long userId, boolean isLiked) {
        this.itemId = itemId;
        this.userId = userId;
        this.isLiked = isLiked;
    }

}
