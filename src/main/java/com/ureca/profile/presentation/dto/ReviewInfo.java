package com.ureca.profile.presentation.dto;

import lombok.Data;

// 리뷰 정보
@Data
public class ReviewInfo {

    // 리뷰 아이디
    private Long reviewId;
    // 리뷰 이미지 URL 1
    private String reviewImgUrl1;
    // 리뷰 이미지 URL 2
    private String reviewImgUrl2;
    // 리뷰 이미지 URL 3
    private String reviewImgUrl3;
    // 디자이너 아이디
    private Long designerId;
    // 디자이너 이미지 URL
    private String designerImgUrl;
    // 디자이너 주소
    private String designerAddress;
    // 리뷰 내용
    private String reviewContents;
    // 별점(1~5)
    private int reviewStar;
    // 좋아요 개수
    private int reviewLikeCnt;
}
