package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Portfolio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    // 디자이너 ID로 포트폴리오 찾기
    List<Portfolio> findByDesignerDesignerId(Long designerId);

    // 디자이너 ID와 포트폴리오 ID로 포트폴리오 찾기
    Portfolio findByDesignerDesignerIdAndPortfolioId(Long designerId, Long portfolioId);
}
