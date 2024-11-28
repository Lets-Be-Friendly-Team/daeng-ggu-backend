package com.ureca.reservation.presentation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationInfo {
    private LocalDate reservationDate; // 예약 날짜
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
}
