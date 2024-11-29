package com.ureca.profile.presentation.dto;

import lombok.Data;

// 견종
@Data
public class Breed {

    // 견종 코드
    private String breedCode;
    // 견종명
    private String codeDesc;

    // 쿼리 결과 담는 생성자
    public Breed(String possibleMajorBreedCode, String codeDesc) {
        this.breedCode = possibleMajorBreedCode;
        this.codeDesc = codeDesc;
    }
}
