package com.ureca.review.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ureca.review.domain.Enum.AuthorType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

// 피드 등록/관리 Dto
public class ReviewDto {
    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Long customerId;
        private Long designerId;
        private Long reviewId;
        private Long userId;
        private AuthorType userType;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime lastCreatedAt;

        private String reviewContents;
        private Double reviewStar;
        private Boolean isFeedAdd;
        private Integer page;
        private List<MultipartFile> FeedImgList;
        private List<String> existImgList;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class Response {
        private Long reviewId;
        private String reviewImgUrl1;
        private String reviewImgUrl2;
        private String reviewImgUrl3;
        private LocalDateTime lastCreatedAt;
        private Long designerId;
        private String designerImgUrl;
        private String designerName;
        private Long customerId;
        private String customerImgUrl;
        private String customerName;
        private String reviewContents;
        private Double reviewStar;
        private Integer reviewLikeCnt;
        private Boolean isReviewLike;
        private Boolean feedExposure;
        private String feedUrl;
        private List<String> FeedImgList;
    }
}
