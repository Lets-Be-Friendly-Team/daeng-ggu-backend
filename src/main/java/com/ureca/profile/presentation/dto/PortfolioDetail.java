package com.ureca.profile.presentation.dto;

import lombok.Data;

// 디자이너 포트폴리오 상세
@Data
public class PortfolioDetail {

    // 디자이너 아이디
    private Long designerId;
    // 포트폴리오 아이디
    private Long portfolioId;
    // 제목
    private String title;
    // 동영상 URL
    private String videoUrl;
    // 동영상명
    private String videoName;
    // 이미지 URL 목록
    private String[] imgUrlList;
    // 내용
    private String contents;
}
