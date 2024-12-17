package com.ureca.monitoring.presentaion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DesignerInfoDto {
	private String designerName;      // 디자이너 이름
	private String address;           // 주소
	private String officialName;      // 미용실 이름 (닉네임)
	private String introduction;      // 소개글
	private String phone;             // 전화번호
	private String designerImgUrl;    // 디자이너 이미지 URL
	private String workExperience;    // 경력
}
