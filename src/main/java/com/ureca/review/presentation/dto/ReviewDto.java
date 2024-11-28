package com.ureca.review.presentation.dto;

import com.ureca.profile.domain.Designer;
import com.ureca.review.domain.ReviewImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public class ReviewDto {
    @Builder
    @Getter
    public static class Request {
        private Long customerId;
        private Long reviewId;
        private String reviewContents;
        private Integer reviewStar;
        private Integer reviewLikeCnt;
        private Boolean isFeedAdd;
        private String feedUrl;
        private Designer designer_id;
        private List<MultipartFile> FeedImgList;
    }

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
        private Integer reviewStar;
        private Integer reviewLikeCnt;
        private Boolean isReviewLike;
        private String feedUrl;
        private List<ReviewImage> reviewImage;
    }
}
