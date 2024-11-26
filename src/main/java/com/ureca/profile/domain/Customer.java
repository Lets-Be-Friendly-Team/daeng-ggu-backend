package com.ureca.profile.domain;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

// 보호자
@Entity
@Table(name = "customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"pets", "bookmarks"})
public class Customer {

    // 보호자 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    // 로그인 아이디
    private String customerLoginId;

    // 이메일
    private String email;

    // 비밀번호
    private String password;

    // 역할
    private String role;

    // 이름
    private String customerName;

    // 생년월일
    private Date birthDate;

    // 성별
    private String gender;

    // 전화번호
    private String phone;

    // 닉네임
    private String nickname;

    // 주소
    private String address;

    // 정보 제공 동의 여부
    private String infoAgree;

    // 보호자 이미지 URL
    private String customerImgUrl;

    // 보호자 이미지명
    private String customerImgName;

    // 가입일자
    private Date joinDate;

    // 보호자와 반려견 연관 관계 (1:N)
    @OneToMany(mappedBy = "customer")
    private List<Pet> pets;

    // 즐겨찾기 연관 관계 (1:N)
    @OneToMany(mappedBy = "customer")
    private List<Bookmark> bookmarks;

    @Builder
    public Customer(
            Long customerId,
            String customerLoginId,
            String email,
            String password,
            String role,
            String customerName,
            Date birthDate,
            String gender,
            String phone,
            String nickname,
            String address,
            String infoAgree,
            String customerImgUrl,
            String customerImgName,
            Date joinDate) {
        this.customerId = customerId;
        this.customerLoginId = customerLoginId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.customerName = customerName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phone = phone;
        this.nickname = nickname;
        this.address = address;
        this.infoAgree = infoAgree;
        this.customerImgUrl = customerImgUrl;
        this.customerImgName = customerImgName;
        this.joinDate = joinDate;
    }
}
