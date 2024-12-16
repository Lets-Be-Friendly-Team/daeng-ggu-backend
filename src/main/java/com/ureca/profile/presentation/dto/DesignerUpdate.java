package com.ureca.profile.presentation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 디자이너 프로필 수정
@Builder
@Getter
public class DesignerUpdate {

    // 디자이너 아이디
    private Long designerId;
    // 닉네임(업체명)
    private String nickname;
    // 신규 디자이너 이미지 URL
    private String newImgUrl;
    // 기존 이미지 URL
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
    // 제공 서비스 코드 목록
    private List<String> providedServices;
    // 미용 가능 견종 코드 목록 (대분류)
    private List<String> possibleBreed;
    // 사업자번호
    private String businessNumber;
    // 사업자인증 여부 (Y/N)
    private String businessIsVerified;
    // 기존 자격증 이미지 URL 목록
    private List<String> preCertifications;
    // 경력사항
    private String workExperience;
    // 신규 자격증 이미지 URL 목록
    private List<String> newCertifications;
}
