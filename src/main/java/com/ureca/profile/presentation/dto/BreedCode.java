package com.ureca.profile.presentation.dto;

import lombok.Builder;
import lombok.Getter;

// 견종 코드
@Builder
@Getter
public class BreedCode {

    // 견종 대분류 코드
    private String majorBreedCode;
    // 견종 소분류 코드
    private String subBreedCode;
}
