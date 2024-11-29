package com.ureca.profile.presentation.dto;

import lombok.Data;

// 서비스
@Data
public class Service {

    // 서비스 코드
    private String servicesCode;
    // 서비스명
    private String codeDesc;

    // 쿼리 결과 담는 생성자
    public Service(String providedServicesCode, String codeDesc) {
        this.servicesCode = providedServicesCode;
        this.codeDesc = codeDesc;
    }
}
