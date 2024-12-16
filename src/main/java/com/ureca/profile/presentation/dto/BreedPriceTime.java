package com.ureca.profile.presentation.dto;

import lombok.Data;

// 견종, 가격, 시간
@Data
public class BreedPriceTime {

    // 견종 대분류 코드
    private String majorBreedCode;
    // 가격
    private String price;
    // 시간
    private String time;
}
