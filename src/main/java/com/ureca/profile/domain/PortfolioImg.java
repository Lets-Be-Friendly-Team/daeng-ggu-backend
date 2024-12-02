package com.ureca.profile.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

// 이미지
@Entity
@Table(name = "portfolio_img")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = "portfolio")
public class PortfolioImg {

    // 이미지 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imgId;

    // 포트폴리오 아이디
    @ManyToOne
    @JoinColumn(name = "portfolioId", nullable = false)
    private Portfolio portfolio;

    // 이미지 URL
    private String imgUrl;

    // 생성시간
    private LocalDateTime createdAt;

    // 수정시간
    @Column(nullable = true)
    private LocalDateTime updatedAt;
}
