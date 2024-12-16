package com.ureca.profile.presentation.dto;

import java.util.List;
import lombok.Data;

// 포트폴리오 정보 - URL 버전
@Data
public class PortfolioInfoUrl {

    // 제목
    private String title;
    // 내용
    private String contents;
    // 동영상 URL
    private String newVideoUrl;
    // 이미지 URL 목록
    private List<String> newImgUrlList;
}
