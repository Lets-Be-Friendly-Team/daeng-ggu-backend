package com.ureca.profile.presentation.dto;

import lombok.Data;

// 찜한 디자이너 정보
@Data
public class BookmarkInfo {

    // 디자이너 아이디
    private Long designerId;
    // 디자이너 이미지 URL
    private String designerImgUrl;
    // 디자이너 주소
    private String designerAddress;
    // 미용 가능 견종
    private String[] possibleBreed;
}
