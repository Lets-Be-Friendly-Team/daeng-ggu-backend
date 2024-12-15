package com.ureca.review.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

// 피드 등록/관리 Dto

public class ReviewDto {
    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ReviewRequest")
    public static class Request {
        private Long designerId;
        private String reviewContents;
        private Double reviewStar;
        private Boolean isFeedAdd;
        //        private List<String> FeedImgList;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ReviewPatch")
    public static class Patch {
        private Long reviewId;
        private String reviewContents;
        private Double reviewStar;
        private Boolean isFeedAdd;
        private List<String> existImgList;
        //        private List<String> FeedImgList;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    @Schema(name = "ReviewResponse")
    public static class Response {
        private Long reviewId;
        private List<String> reviewImgList;
        private LocalDateTime lastCreatedAt;
        private Long designerId;
        private String designerImgUrl;
        private String designerAddress;
        private String nickname;
        private String designerName;
        private Long customerId;
        private String customerImgUrl;
        private String customerName;
        private String reviewContents;
        private Double reviewStar;
        private Integer reviewLikeCnt;
        private Boolean isReviewLike;
        private Boolean feedExposure;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ReviewCustomer")
    public static class Customer {
        private Long reviewId;
        private List<String> reviewImgList;
        private Long designerId;
        private String designerImgUrl;
        private String designerName;
        private String designerAddress;
        private String nickname;
        private String reviewContents;
        private Double reviewStar;
        private Integer reviewLikeCnt;
        private Boolean isReviewLike;
        private Boolean feedExposure;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ReviewDesigner")
    public static class Designer {
        private Long reviewId;
        private List<String> reviewImgList;
        private Long customerId;
        private String customerImgUrl;
        private String customerName;
        private String reviewContents;
        private Double reviewStar;
        private Integer reviewLikeCnt;
        private Boolean isReviewLike;
        private Boolean feedExposure;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ReviewID")
    public static class ID {
        private Long reviewId;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ReviewFeed")
    public static class Feed {
        private Integer page;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ReviewLike")
    public static class Like {
        private Integer totalReview;
        private List<Response> reviewList;
    }
}
