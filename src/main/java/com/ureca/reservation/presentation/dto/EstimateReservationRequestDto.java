package com.ureca.reservation.presentation.dto;

import java.math.BigDecimal;
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
public class EstimateReservationRequestDto {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;

    private Long customerId;
    private Long estimateId;
    private LocalDate reservationDate; // 예약 일자 (예: 2024-12-01)
    private LocalTime startTime; // 시작 시간 (예: 10:00:00)
    private LocalTime endTime; // 종료 시간 (예: 10:00:00)
    private BigDecimal groomingFee;
    private BigDecimal deliveryFee;
    private BigDecimal monitoringFee;
    private BigDecimal totalPayment;
}
