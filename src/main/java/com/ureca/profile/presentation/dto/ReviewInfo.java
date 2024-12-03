package com.ureca.profile.presentation.dto;

import lombok.Data;

// 리뷰 정보
@Data
public class ReviewInfo {

    // 리뷰 아이디
    private Long reviewId;
    // 리뷰 썸네일 이미지 URL
    private String reviewImgUrl;
}
