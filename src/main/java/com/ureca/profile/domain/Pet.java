package com.ureca.profile.domain;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

// 반려견
@Entity
@Table(name = "pet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public Pet(
            Long petId,
            Customer customer,
            String petName,
            Date birthDate,
            String gender,
            String majorBreedCode,
            String subBreedCode,
            Double weight,
            String specialNotes,
            String petImgUrl,
            String petImgName,
            String isNeutered) {
        this.petId = petId;
        this.customer = customer;
        this.petName = petName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.majorBreedCode = majorBreedCode;
        this.subBreedCode = subBreedCode;
        this.weight = weight;
        this.specialNotes = specialNotes;
        this.petImgUrl = petImgUrl;
        this.petImgName = petImgName;
        this.isNeutered = isNeutered;
    }
}
