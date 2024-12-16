package com.ureca.profile.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import lombok.*;

// 디자이너
@Entity
@Table(name = "designer")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = {"bookmarks", "services", "breeds", "certificates", "portfolios"})
public class Designer {

    // 디자이너 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designerId;

    // 로그인 아이디 (이메일+"_"+고유값)
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

    // 전화번호
    private String phone;

    // 빌링코드
    private String billingCode;

    // 월정액 결제 여부
    private String isMonthlyPay;

    // 월정액 결제 일자
    private LocalDateTime monthlyPayDate;

    // 디자이너 이미지 URL
    private String designerImgUrl;

    // 디자이너 이미지명
    private String designerImgName;

    // 생년월일
    private LocalDate birthDate;

    // 성별
    private String gender;

    // 주소1
    private String address1;

    // 주소2
    private String address2;

    // 상세 주소
    private String detailAddress;

    // x좌표 (경도)
    private double xPosition;

    // y좌표 (위도)
    private double yPosition;

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

    // 휴무일
    private String dayOff;

    // 즐겨찾기 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer")
    private List<Bookmark> bookmarks;

    // 제공 서비스 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer", cascade = CascadeType.ALL)
    private List<Services> services;

    // 가능 견종 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer", cascade = CascadeType.ALL)
    private List<Breeds> breeds;

    // 인증서 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer")
    private List<Certificate> certificates;

    // 포트폴리오 연관 관계 (1:N)
    @OneToMany(mappedBy = "designer")
    private List<Portfolio> portfolios;

    // 생성시간
    private LocalDateTime createdAt;

    // 수정시간
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    public void updateBillingCode(String billingCode) {
        this.billingCode = billingCode;
    }
}
