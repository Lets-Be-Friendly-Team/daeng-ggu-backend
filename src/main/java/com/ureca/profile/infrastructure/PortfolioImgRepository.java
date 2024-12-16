package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Portfolio;
import com.ureca.profile.domain.PortfolioImg;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioImgRepository extends JpaRepository<PortfolioImg, Long> {
    // 포트폴리오 ID로 이미지 찾기
    List<PortfolioImg> findByPortfolioPortfolioId(Long portfolioId);

    // Portfolio 기준으로 imgUrl 리스트를 찾기
    List<PortfolioImg> findByPortfolio(Portfolio portfolio);

    @Modifying
    @Query("DELETE FROM PortfolioImg p WHERE p.portfolio = :portfolio AND p.imgUrl = :imgUrl")
    void deleteByPortfolioAndImgUrl(Portfolio portfolio, String imgUrl);

    // 포트폴리오 이미지 URL 목록 조회
    @Query("SELECT i.imgUrl FROM PortfolioImg i WHERE i.portfolio.portfolioId = :portfolioId")
    List<String> findImgUrlByPortfolioPortfolioId(Long portfolioId);
}
