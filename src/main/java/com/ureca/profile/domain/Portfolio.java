package com.ureca.profile.domain;

import jakarta.persistence.*;
import lombok.*;

// 포트폴리오
@Entity
@Table(name = "portfolio")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "designer")
public class Portfolio {

    // 포트폴리오 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioId;

    // 디자이너 아이디
    @ManyToOne
    @JoinColumn(name = "designerId", nullable = false)
    private Designer designer;

    // 동영상 URL
    private String videoUrl;

    // 동영상명
    private String videoName;

    // 제목
    private String title;

    // 내용
    private String contents;

    @Builder
    public Portfolio(
            Long portfolioId,
            Designer designer,
            String videoUrl,
            String videoName,
            String title,
            String contents) {
        this.portfolioId = portfolioId;
        this.designer = designer;
        this.videoUrl = videoUrl;
        this.videoName = videoName;
        this.title = title;
        this.contents = contents;
    }
}
