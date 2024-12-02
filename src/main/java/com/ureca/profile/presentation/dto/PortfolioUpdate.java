package com.ureca.profile.presentation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

// 디자이너 포트폴리오 등록/수정
@Builder
@Getter
public class PortfolioUpdate {

    // 디자이너 아이디
    private Long designerId;
    // 포트폴리오 아이디
    private Long portfolioId;
    // 제목
    private String title;
    // 내용
    private String contents;
    // 이전 동영상 URL
    private String preVideoUrl;
    // 신규 동영상 파일
    private MultipartFile newVideoFile;
    // 기존 이미지 URL 목록
    private String[] preImgUrlList;
    // 신규 이미지 파일 목록
    private List<MultipartFile> newImgFileList;
}
