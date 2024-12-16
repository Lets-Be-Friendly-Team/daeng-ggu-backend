package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Services;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {
    // 디자이너 ID로 제공 서비스 찾기
    List<Services> findByDesignerDesignerId(Long designerId);

    // 디자이너와 서비스 코드로 제공 서비스 찾기
    Optional<Services> findByDesignerAndProvidedServicesCode(
            Designer designer, String providedServicesCode);

    @Query("SELECT s.designer FROM Services s WHERE s.providedServicesCode = :desiredServiceCode")
    List<Designer> findDesignerByProvidedServicesCode(String desiredServiceCode);

    // providedServicesCode와 designer로 Services 조회
    List<Services> findByProvidedServicesCodeAndDesigner(
            String providedServicesCode, Designer designer);

    // 디자이너로 Services 목록 조회
    List<Services> findByDesigner(Designer designer);

    // 디자이너 ID로 제공된 서비스 코드 조회
    @Query(
            "SELECT s.providedServicesCode FROM Services s WHERE s.designer.designerId = :designerId")
    List<String> findProvidedServicesByDesignerId(Long designerId);

    // 디자이너로 제공된 서비스 코드 조회
    @Query("SELECT s.providedServicesCode FROM Services s WHERE s.designer = :designer")
    List<String> findProvidedServicesByDesigner(Designer designer);

    // 디자이너와 제공 서비스 코드로 서비스 삭제
    @Modifying
    @Transactional
    @Query(
            "DELETE FROM Services s WHERE s.designer = :designer AND s.providedServicesCode = :providedServicesCode")
    void deleteByDesignerAndProvidedServicesCode(Designer designer, String providedServicesCode);
}
