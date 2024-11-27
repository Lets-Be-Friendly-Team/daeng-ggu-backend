package com.ureca.reservation.infrastructure;

import com.ureca.reservation.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 보호자 ID를 기준으로 예약 내역 조회, 최근 데이터가 맨 앞으로 오도록 정렬
    @Query(
            "SELECT r FROM Reservation r "
                    + "JOIN r.pet p "
                    + "WHERE p.customer.customerId = :customerId "
                    + "ORDER BY r.reservationDate DESC, r.startTime DESC")
    List<Reservation> findAllByCustomerId(@Param("customerId") Long customerId);
}
