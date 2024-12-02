package com.ureca.profile.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import lombok.*;

// 반려견
@Entity
@Table(name = "pet")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = "customer")
public class Pet {

    // 반려견 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    // 보호자 아이디
    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    // 반려견 이름
    private String petName;

    // 반려견 생년월일
    private Date birthDate;

    // 반려견 성별
    private String gender;

    // 대부류 견종코드
    private String majorBreedCode;

    // 소분류 견종코드
    private String subBreedCode;

    // 반려견 무게
    private Double weight;

    // 특이사항
    private String specialNotes;

    // 반려견 이미지 URL
    private String petImgUrl;

    // 반려견 이미지명
    private String petImgName;

    // 중성화 여부
    private String isNeutered;

    // 생성시간
    private LocalDateTime createdAt;

    // 수정시간
    @Column(nullable = true)
    private LocalDateTime updatedAt;
}
