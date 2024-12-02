package com.ureca.profile.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

// 반려견 프로필 등록/수정
@Builder
@Getter
public class PetUpdate {

    // 보호자 아이디
    private Long customerId;
    // 반려견 아이디
    private Long petId;
    // 반려견명
    private String petName;
    // 신규 반려견 이미지 파일
    private MultipartFile newPetImgFile;
    // 변경전 이미지 Url
    private String prePetImgUrl;
    // 견종 대분류 코드
    private String majorBreedCode;
    // 견종 소분류 코드
    private String subBreedCode;
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
