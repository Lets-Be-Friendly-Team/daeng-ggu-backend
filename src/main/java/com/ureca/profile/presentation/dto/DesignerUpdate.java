package com.ureca.profile.presentation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

// 디자이너 프로필 등록/수정
@Builder
@Getter
public class DesignerUpdate {

    // 디자이너 아이디
    private Long designerId;
    // 디자이너 아이디
    private String designerName;
    // 닉네임(업체명)
    private String nickname;
    // 신규 디자이너 이미지 파일
    private MultipartFile newImgFile;
    // 변경전 이미지 URL
    private String preImgUrl;
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
    // 제공 서비스 코드
    private String[] providedServices;
    // 미용 가능 견종 코드
    private List<BreedCode> possibleBreed;
    // 디자이너 로그인 아이디
    private String designerLoginId;
    // 본인인증 여부 (Y/N)
    private String isVerified;
    // 사업자번호
    private String businessNumber;
    // 사업자인증 여부 (Y/N)
    private String businessIsVerified;
    // 자격증 이미지 URL
    private String[] certifications;
    // 경력사항
    private String workExperience;
    // 신규 자격증 이미지 목록
    private List<MultipartFile> certificationsFileList;
    // 포트폴리오 목록
    private List<PortfolioInfo> portfolioList;
}
