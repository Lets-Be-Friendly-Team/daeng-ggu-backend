package com.ureca.profile.domain;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

// 디자이너
@Entity
@Table(name = "designer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"bookmarks", "services", "breeds", "certificates", "portfolios"})
public class Designer {

    // 디자이너 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designerId;

    // 로그인 아이디
    private String designerLoginId;

    // 이메일
    private String email;

    // 비밀번호
    private String password;

    // 역할
    private String role;

    // 이름
    private String designerName;

    // 닉네임(업체명)
    private String officialName;

    // 가입일자
    private Date joinDate;

    // 빌링코드
    private String billingCode;

    // 월정액 결제 여부
    private String isMonthlyPay;

    // 월정액 결제 일자
    private Date monthlyPayDate;

    // 월정액 결제 유지 달
    private String monthlyPayMM;

    // 디자이너 이미지 URL
    private String designerImgUrl;

    // 디자이너 이미지명
    private String designerImgName;

    // 주소
    private String address;

    // 소개글
    private String introduction;

    // 경력사항
    private String workExperience;

    // 본인 인증 여부
    private String isVerified;

    // 사업자 번호
    private String businessNumber;

    // 사업자 인증 여부
    private String businessIsVerified;

    // 즐겨찾기 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer")
    private List<Bookmark> bookmarks;

    // 제공 서비스 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer")
    private List<Services> services;

    // 가능 견종 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer")
    private List<Breeds> breeds;

    // 인증서 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer")
    private List<Certificate> certificates;

    // 포트폴리오 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer")
    private List<Portfolio> portfolios;

    @Builder
    public Designer(
            Long designerId,
            String designerLoginId,
            String email,
            String password,
            String role,
            String designerName,
            String officialName,
            Date joinDate,
            String billingCode,
            String isMonthlyPay,
            Date monthlyPayDate,
            String designerImgUrl,
            String designerImgName,
            String address,
            String introduction,
            String workExperience,
            String isVerified,
            String businessNumber,
            String businessIsVerified) {
        this.designerId = designerId;
        this.designerLoginId = designerLoginId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.designerName = designerName;
        this.officialName = officialName;
        this.joinDate = joinDate;
        this.billingCode = billingCode;
        this.isMonthlyPay = isMonthlyPay;
        this.monthlyPayDate = monthlyPayDate;
        this.designerImgUrl = designerImgUrl;
        this.designerImgName = designerImgName;
        this.address = address;
        this.introduction = introduction;
        this.workExperience = workExperience;
        this.isVerified = isVerified;
        this.businessNumber = businessNumber;
        this.businessIsVerified = businessIsVerified;
    }
}
