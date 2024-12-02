package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.PortfolioImg;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioImgRepository extends JpaRepository<PortfolioImg, Long> {
    // 포트폴리오 ID로 이미지 찾기
    List<PortfolioImg> findByPortfolioPortfolioId(Long portfolioId);

    // 포트폴리오 이미지 URL 목록 조회
    @Query("SELECT i.imgUrl FROM PortfolioImg i WHERE i.portfolio.portfolioId = :portfolioId")
    List<String> findImgUrlByPortfolioPortfolioId(Long portfolioId);
}
