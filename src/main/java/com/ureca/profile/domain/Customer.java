package com.ureca.profile.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import lombok.*;

// 보호자
@Entity
@Table(name = "customer")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = {"pets", "bookmarks"})
public class Customer {

    // 보호자 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    // 로그인 아이디 (이메일+고유값)
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

    // 주소1
    private String address1;

    // 주소2
    private String address2;

    // 상세 주소
    private String detailAddress;

    // 정보 제공 동의 여부
    private String infoAgree;

    // 보호자 이미지 URL
    private String customerImgUrl;

    // 보호자 이미지명
    private String customerImgName;

    // 생성시간
    private LocalDateTime createdAt;

    // 수정시간
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    // 보호자와 반려견 연관 관계 (1:N)
    @OneToMany(mappedBy = "customer")
    private List<Pet> pets;

    // 즐겨찾기 연관 관계 (1:N)
    @OneToMany(mappedBy = "customer")
    private List<Bookmark> bookmarks;
}
