package com.ureca.profile.presentation.dto;

import lombok.*;

// 보호자 프로필 등록/수정
@Builder
@Getter
public class CustomerUpdate {

    // 보호자 아이디
    private Long customerId;
    // 보호자명
    private String customerName;
    // 변경전 이미지 URL
    private String preCustomerImgUrl;
    // 생년월일
    private String birthDate;
    // 성별
    private String gender;
    // 전화번호
    private String phone;
    // 닉네임
    private String nickname;
    // 기본주소1
    private String address1;
    // 기본주소2
    private String address2;
    // 상세 주소
    private String detailAddress;
}
