package com.ureca.profile.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

// 포트폴리오
@Entity
@Table(name = "portfolio")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    // 포트폴리오와 포트폴리오 이미지 연관 관계 (1:N)
    @OneToMany(mappedBy = "portfolio")
    private List<PortfolioImg> PortfolioImgs;

    // 생성시간
    private LocalDateTime createdAt;

    // 수정시간
    @Column(nullable = true)
    private LocalDateTime updatedAt;
}
