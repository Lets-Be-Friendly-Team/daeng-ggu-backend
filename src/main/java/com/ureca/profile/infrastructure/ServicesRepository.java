package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Services;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {
    // 디자이너 ID로 제공 서비스 찾기
    List<Services> findByDesignerDesignerId(Long designerId);
}
