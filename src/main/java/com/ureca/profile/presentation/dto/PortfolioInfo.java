package com.ureca.profile.presentation.dto;

import lombok.Data;

// 포트폴리오 정보
@Data
public class PortfolioInfo {

    // 포트폴리오 아이디
    private Long portfolioId;
    // 제목
    private String title;
    // 동영상 URL
    private String videoUrl;
    // 이미지 URL 목록
    private String[] imgUrlList;
    // 내용
    private String contents;

    // 쿼리에서 사용할 생성자
    public PortfolioInfo(Long portfolioId, String title, String videoUrl, String contents) {
        this.portfolioId = portfolioId;
        this.title = title;
        this.videoUrl = videoUrl;
        this.contents = contents;
    }
}
