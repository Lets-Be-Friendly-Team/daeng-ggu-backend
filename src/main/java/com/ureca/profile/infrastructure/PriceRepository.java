package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Price;
import com.ureca.profile.domain.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    // 서비스 엔티티 기준으로 관련된 가격 데이터를 삭제하는 메서드
    void deleteByService(Services service);

    @Modifying
    @Transactional
    @Query(
            "DELETE FROM Price p WHERE p.service.serviceId IN ("
                    + "SELECT s.serviceId FROM Services s WHERE s.providedServicesCode = :providedServicesCode AND s.designer.designerId = :designerId)")
    void deleteByServiceCodeAndDesigner(
            @Param("providedServicesCode") String providedServicesCode,
            @Param("designerId") Long designerId);
}
