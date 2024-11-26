package com.ureca.profile.domain;

import jakarta.persistence.*;
import lombok.*;

// 가능 견종
@Entity
@Table(name = "breeds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "designer")
public class Breeds {

    // 가능 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long possibleId;

    // 디자이너 아이디
    @ManyToOne
    @JoinColumn(name = "designerId", nullable = false)
    private Designer designer;

    // 미용 가능 대분류 견종코드
    private String possibleMajorBreedCode;

    // 미용 가능 소분류 견종코드
    private String possibleSubBreedCode;

    @Builder
    public Breeds(
            Long possibleId,
            Designer designer,
            String possibleMajorBreedCode,
            String possibleSubBreedCode) {
        this.possibleId = possibleId;
        this.designer = designer;
        this.possibleMajorBreedCode = possibleMajorBreedCode;
        this.possibleSubBreedCode = possibleSubBreedCode;
    }
}
