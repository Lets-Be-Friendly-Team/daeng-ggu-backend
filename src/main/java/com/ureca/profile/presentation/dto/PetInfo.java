package com.ureca.profile.presentation.dto;

import lombok.Data;

// 반려견 정보
@Data
public class PetInfo {

    // 반려견 아이디
    private Long petId;
    // 반려견 이름
    private String petName;
    // 반려견 이미지 URL
    private String petImgUrl;
}
