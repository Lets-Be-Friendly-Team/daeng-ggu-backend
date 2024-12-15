package com.ureca.review.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    @Schema(name = "ReviewLikeResponse")
    public static class Response {
        private Integer reviewLikeCnt;
        private Boolean isReviewLike;
    }
}
