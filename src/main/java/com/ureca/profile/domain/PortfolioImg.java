package com.ureca.profile.domain;

import jakarta.persistence.*;
import lombok.*;

// 이미지
@Entity
@Table(name = "portfolio_img")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public PortfolioImg(Long imgId, Portfolio portfolio, String imgUrl) {
        this.imgId = imgId;
        this.portfolio = portfolio;
        this.imgUrl = imgUrl;
    }
}
