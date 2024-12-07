package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Services;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {
    // 디자이너 ID로 제공 서비스 찾기
    List<Services> findByDesignerDesignerId(Long designerId);

    // 디자이너와 서비스 코드로 제공 서비스 찾기
    Optional<Services> findByDesignerAndProvidedServicesCode(
            Designer designer, String providedServicesCode);

    @Query("SELECT s.designer FROM Services s WHERE s.providedServicesCode = :desiredServiceCode")
    List<Designer> findDesignerByProvidedServicesCode(String desiredServiceCode);
}
