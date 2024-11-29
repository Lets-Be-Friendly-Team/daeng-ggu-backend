package com.ureca.profile.presentation.dto;

import lombok.Data;

// 반려견 프로필 상세
@Data
public class PetDetail {

    // 반려견 아이디
    private Long petId;
    // 반려견 이름
    private String petName;
    // 반려견 이미지 URL
    private String petImgUrl;
    // 반려견 이미지명
    private String petImgName;
    // 견종 대분류 코드
    private String majorBreedCode;
    // 견종 대분류명
    private String majorBreed;
    // 견종 소분류 코드
    private String subBreedCode;
    // 견종 소분류명
    private String subBreed;
    // 생년월일 (YYYYMMDD)
    private String birthDate;
    // 성별 (M/W)
    private String gender;
    // 중성화 여부 (Y/N)
    private String isNeutered;
    // 몸무게
    private Double weight;
    // 특이사항
    private String specialNotes;
}
