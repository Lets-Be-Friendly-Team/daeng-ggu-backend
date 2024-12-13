package com.ureca.profile.presentation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

// 보호자가 보는 디자이너 프로필
@Data
@Builder
public class CustomerViewDesignerProfile {

    // 찜 여부
    private Boolean isBookmarked;
    // 디자이너 아이디
    private Long designerId;
    // 디자이너명
    private String designerName;
    // 닉네임(업체명)
    private String nickname;
    // 디자이너 이미지 URL
    private String designerImgUrl;
    // 디자이너 이미지명
    private String designerImgName;
    // 별점 평균
    private Double reviewStarAvg;
    // 리뷰 전체 좋아요 수
    private int reviewLikeCntAll;
    // 기본주소1
    private String address1;
    // 기본주소2
    private String address2;
    // 상세 주소
    private String detailAddress;
    // 제공 서비스
    private List<Service> providedServices;
    // 미용 가능 견종
    private List<Breed> possibleBreeds;
    // 소개글
    private String introduction;
    // 경력사항
    private String workExperience;
    // 자격증 이미지 URL
    private String[] certifications;
    // 포트폴리오 목록
    private List<PortfolioInfo> portfolioList;
    // 리뷰 목록
    private List<ReviewInfo> reviewList;
}
