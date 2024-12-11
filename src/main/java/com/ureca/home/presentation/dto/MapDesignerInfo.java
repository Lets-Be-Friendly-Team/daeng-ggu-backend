package com.ureca.home.presentation.dto;

import lombok.Data;

// 지도 디자이너 검색 조회
@Data
public class MapDesignerInfo {

    private Long designerId; // 디자이너 아이디
    private String designerName; // 디자이너명
    private String nickname; // 닉네임(업체명)
    private Double lng; // x좌표
    private Double lat; // y좌표
    private String address1; // 기본주소1
    private String address2; // 기본주소2
    private String detailAddress; // 상세 주소

    // 쿼리 결과 담는 생성자
    public MapDesignerInfo(
            Long designerId,
            String designerName,
            String officialName,
            Double lng,
            Double lat,
            String address1,
            String address2,
            String detailAddress) {
        this.designerId = designerId;
        this.designerName = designerName;
        this.nickname = officialName;
        this.lng = lng;
        this.lat = lat;
        this.address1 = address1;
        this.address2 = address2;
        this.detailAddress = detailAddress;
    }
}
