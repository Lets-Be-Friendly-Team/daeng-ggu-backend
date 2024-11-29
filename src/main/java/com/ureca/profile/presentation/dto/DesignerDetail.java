package com.ureca.profile.presentation.dto;

import java.util.List;
import lombok.Data;

// 디자이너 프로필 상세
@Data
public class DesignerDetail {

    // 디자이너 아이디
    private Long designerId;
    // 디자이너명
    private String designerName;
    // 닉네임(업체명)
    private String nickname;
    // 디자이너 이미지 URL
    private String designerImgUrl;
    // 디자이너 이미지명
    private String designerImgName;
    // 기본주소1
    private String address1;
    // 기본주소2
    private String address2;
    // 상세 주소
    private String detailAddress;
    // 소개글
    private String introduction;
    // 연락처
    private String phone;
    // 제공 서비스
    private List<Service> providedServices;
    // 미용 가능 견종
    private List<Breed> possibleBreeds;
    // 디자이너 이메일
    private String email;
    // 사업자번호
    private String businessNumber;
    // 자격증 이미지 URL
    private String[] certifications;
    // 경력사항
    private String workExperience;
    // 포트폴리오 목록
    private List<PortfolioInfo> portfolioList;

    // 쿼리에서 사용할 생성자
    public DesignerDetail(
            Long designerId,
            String designerName,
            String officialName,
            String designerImgUrl,
            String designerImgName,
            String address1,
            String address2,
            String detailAddress,
            String introduction,
            String phone,
            String email,
            String businessNumber,
            String workExperience) {
        this.designerId = designerId;
        this.designerName = designerName;
        this.nickname = officialName;
        this.designerImgUrl = designerImgUrl;
        this.designerImgName = designerImgName;
        this.address1 = address1;
        this.address2 = address2;
        this.detailAddress = detailAddress;
        this.introduction = introduction;
        this.phone = phone;
        this.email = email;
        this.businessNumber = businessNumber;
        this.workExperience = workExperience;
    }
}
