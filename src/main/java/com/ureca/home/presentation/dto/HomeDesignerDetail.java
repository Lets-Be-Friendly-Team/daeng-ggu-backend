package com.ureca.home.presentation.dto;

import lombok.Data;

// 디자이너 찾기 - 디자이너 상세
@Data
public class HomeDesignerDetail {

    private Long designerId; // 디자이너 아이디
    private String designerName; // 디자이너명
    private String nickname; // 닉네임(업체명)
    private String designerImgUrl; // 디자이너 이미지 URL
    private Double reviewStarAvg; // 별점 평균
    private Long bookmarkCnt; // 찜한 사람 수
    private String address1; // 기본주소1
    private String address2; // 기본주소2
    private String detailAddress; // 상세 주소
    private String[] possibleBreed; // 미용 가능 견종
    private Double xPosition; // x좌표
    private Double yPosition; // y좌표

    // 쿼리 결과 담는 생성자
    public HomeDesignerDetail(
            Long designerId,
            String designerName,
            String nickname,
            String designerImgUrl,
            Double reviewStarAvg,
            Long bookmarkCnt,
            String address1,
            String address2,
            String detailAddress,
            String[] possibleBreed,
            Double xPosition,
            Double yPosition) {
        this.designerId = designerId;
        this.designerName = designerName;
        this.nickname = nickname;
        this.designerImgUrl = designerImgUrl;
        this.reviewStarAvg = reviewStarAvg;
        this.bookmarkCnt = bookmarkCnt;
        this.address1 = address1;
        this.address2 = address2;
        this.possibleBreed = possibleBreed;
        this.detailAddress = detailAddress;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}
