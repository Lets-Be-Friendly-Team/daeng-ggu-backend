package com.ureca.profile.presentation.dto;

import lombok.Data;

// 리뷰 정보
@Data
public class ReviewInfo {

    // 리뷰 아이디
    private Long reviewId;
    // 리뷰 썸네일 이미지 URL
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
    // 디자이너명
    private String designerName;
    // 디자이너 닉네임(업체명)
    private String officialName;
    // 보호자 아이디
    private Long customerId;
    // 보호자 이미지 URL
    private String customerImgUrl;
    // 보호자명
    private String customerName;
    // 보호자 닉네임
    private String nickname;
    // 리뷰 내용
    private String reviewContents;
    // 별점(1~5)
    private Double reviewStar;
    // 좋아요 개수
    private int reviewLikeCnt;
    // 리뷰 피드 노출 유무 (true:공개, false:비공개)
    private Boolean feedExposure;
}
