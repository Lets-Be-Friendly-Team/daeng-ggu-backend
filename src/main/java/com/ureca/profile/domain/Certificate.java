package com.ureca.profile.domain;

import jakarta.persistence.*;
import lombok.*;

// 인증서
@Entity
@Table(name = "certificate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "designer")
public class Certificate {

    // 인증서 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;

    // 디자이너 아이디
    @ManyToOne
    @JoinColumn(name = "designerId", nullable = false)
    private Designer designer;

    // 이미지 URL
    private String imgUrl;

    @Builder
    public Certificate(Long certificateId, Designer designer, String imgUrl) {
        this.certificateId = certificateId;
        this.designer = designer;
        this.imgUrl = imgUrl;
    }
}
