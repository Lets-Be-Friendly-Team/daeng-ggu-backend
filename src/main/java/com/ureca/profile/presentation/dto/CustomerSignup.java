package com.ureca.profile.presentation.dto;

import lombok.Data;

// 보호자 회원가입 정보
@Data
public class CustomerSignup {

    // 보호자명
    private String customerName;
    // 생년월일 (YYYYMMDD)
    private String birthDate;
    // 성별 (남M/여F)
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
