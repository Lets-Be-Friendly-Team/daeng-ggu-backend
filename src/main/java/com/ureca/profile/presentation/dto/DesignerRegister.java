package com.ureca.profile.presentation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 디자이너 프로필 등록
@Builder
@Getter
public class DesignerRegister {

    // 디자이너 아이디
    private Long designerId;
    // 닉네임(업체명)
    private String nickname;
    // 신규 디자이너 이미지 Url
    private String newImgUrl;
    // 기본주소1
    private String address1;
    // 기본주소2
    private String address2;
    // 상세 주소
    private String detailAddress;
    // 소개글
    private String introduction;
    // 연락처
    private String phone;
    // 휴무일
    private String[] dayOff;
    // 서비스별 견종 가격 및 시간 목록
    private List<ProvidedServices> providedServiceList;
    // 사업자번호
    private String businessNumber;
    // 사업자인증 여부 (Y/N)
    private String businessIsVerified;
    // 신규 자격증 이미지 URL 목록
    private List<String> certificationsUrlList;
    // 경력사항
    private String workExperience;
    // 포트폴리오 목록
    private List<PortfolioInfoUrl> portfolioList;
}
