package com.ureca.review.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestBody;

public class ReviewLikeDto {
    @Builder
    @Getter
    public static class Request {
        Long userId;
        String userType;
        Long reviewId;
    }
    @Builder
    @Getter
    public static class Response {}
}
