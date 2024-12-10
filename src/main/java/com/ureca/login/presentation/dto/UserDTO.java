package com.ureca.login.presentation.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTO {

    // 회원유무 (Y:로그인, N:회원가입)
    private String joinYn;
    // 사용자 유형 (C:보호자, D:디자이너)
    private String userType;
    // 사용자 ID값 (customerId or designerId)
    private long id;
    // 이메일
    private String email;
    // 로그인 아이디 (고유값)
    private String loginId;
    // 리프레시 토큰
    private String refreshToken;
}
