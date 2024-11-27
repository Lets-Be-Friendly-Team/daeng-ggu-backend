package com.ureca.profile.presentation.dto;

import java.util.List;
import lombok.Data;

// 보호자 프로필
@Data
public class CustomerProfile {

    // 보호자 아이디
    private Long customerId;
    // 보호자명
    private String customerName;
    // 보호자 이미지 URL
    private String customerImgUrl;
    // 보호자 이미지명
    private String customerImgName;
    // 닉네임
    private String nickname;
    // 반려견 목록
    private List<PetInfo> petList;
    // 리뷰 목록
    private List<ReviewInfo> reviewList;
    // 찜한목록
    private List<BookmarkInfo> bookmarkList;
}
