package com.ureca.reservation.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignerInfoDto {

    // 디자이너 아이디
    private Long designerId;

    // 이름
    private String designerName;

    // 닉네임(업체명)
    private String officialName;

    // 디자이너 이미지 URL
    private String designerImgUrl;

    // 디자이너 이미지명
    private String designerImgName;

    // 주소1
    private String address1;

    // 주소2
    private String address2;

    // 상세 주소
    private String detailAddress;

    // 소개글
    private String introduction;

    // 경력사항
    private String workExperience;

    // 사업자 번호
    private String businessNumber;

    // 사업자 인증 여부
    private String businessIsVerified;
}
