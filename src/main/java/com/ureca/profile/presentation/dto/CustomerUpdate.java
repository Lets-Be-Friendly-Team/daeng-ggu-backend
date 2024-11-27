package com.ureca.profile.presentation.dto;

import java.io.File;
import lombok.Data;

// 보호자 프로필 등록/수정
@Data
public class CustomerUpdate {

    // 보호자 아이디
    private Long customerId;
    // 보호자 로그인 아이디
    private String customerLoginId;
    // 보호자명
    private String customerName;
    // 신규 보호자 이미지 파일
    private File newCustomerImgFile;
    // 변경전 이미지명
    private String preCustomerImgName;
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
