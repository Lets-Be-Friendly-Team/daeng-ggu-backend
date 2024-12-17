package com.ureca.reservation.infrastructure;

import com.ureca.reservation.domain.Reservation;
import com.ureca.reservation.presentation.dto.ReservationInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationId(Long reservationId);

    // 보호자 ID를 기준으로 예약 내역 조회, 최근 데이터가 맨 앞으로 오도록 정렬
    @Query(
            "SELECT r FROM Reservation r "
                    + "JOIN r.pet p "
                    + "WHERE p.customer.customerId = :customerId "
                    + "ORDER BY r.reservationDate DESC, r.startTime DESC")
    List<Reservation> findAllByCustomerId(@Param("customerId") Long customerId);

    // 디자이너 ID를 기준으로 예약 내역 조회, 최근 데이터가 맨 앞으로 오도록 정렬

    @Query(
            "SELECT r FROM Reservation r "
                    + "WHERE r.designer.designerId = :designerId "
                    + "ORDER BY r.reservationDate DESC, r.startTime DESC")
    List<Reservation> findAllByDesignerId(@Param("designerId") Long designerId);

    // 디자이너 ID와 연도, 월에 해당하는 예약 정보를 ReservationInfo DTO로 조회
    @Query(
            "SELECT new com.ureca.reservation.presentation.dto.ReservationInfo(r.reservationDate, r.startTime, r.endTime) "
                    + "FROM Reservation r WHERE r.designer.designerId = :designerId "
                    + "AND YEAR(r.reservationDate) = :year AND MONTH(r.reservationDate) = :month")
    List<ReservationInfo> findReservationsByDesignerAndMonth(Long designerId, int year, int month);

    // isDelivery가 true, isFinished가 false인 예약을 날짜와 시간 기준으로 정렬하여 조회
    List<Reservation> findByIsDeliveryTrueAndIsFinishedFalseOrderByReservationDateAscStartTimeAsc();
}
