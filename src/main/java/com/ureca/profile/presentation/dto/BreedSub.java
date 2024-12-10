package com.ureca.profile.presentation.dto;

import lombok.Data;

// 견종
@Data
public class BreedSub {

    // 견종 코드
    private String breedCode;
    // 견종명
    private String codeDesc;

    // 쿼리 결과 담는 생성자
    public BreedSub(String possibleSubBreedCode, String codeDesc) {
        this.breedCode = possibleSubBreedCode;
        this.codeDesc = codeDesc;
    }
}
