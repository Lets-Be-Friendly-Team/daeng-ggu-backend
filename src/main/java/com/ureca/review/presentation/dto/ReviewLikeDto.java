package com.ureca.review.presentation.dto;

import lombok.Builder;
import lombok.Getter;

public class ReviewLikeDto {
    @Builder
    @Getter
    public static class Request {
        Long userId;
        String userType;
        Long reviewId;
    }
}
