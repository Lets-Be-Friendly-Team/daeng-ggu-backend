package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Img;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgRepository extends JpaRepository<Img, Long> {
    // 포트폴리오 ID로 이미지 찾기
    List<Img> findByPortfolioPortfolioId(Long portfolioId);
}
