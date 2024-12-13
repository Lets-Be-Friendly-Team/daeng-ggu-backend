package com.ureca.profile.presentation.dto;

import lombok.Data;

// 디자이너 회원가입 정보
@Data
public class DesignerSignup {

    // 디자이너명
    private String designerName;
    // 생년월일 (YYYYMMDD)
    private String birthDate;
    // 성별 (남M/여F)
    private String gender;
    // 전화번호
    private String phone;
    // 닉네임(업체명)
    private String nickname;
}
