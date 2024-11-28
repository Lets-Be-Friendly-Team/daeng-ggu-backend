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
public class ReservationHistoryResponseDto {
    private Long reservationId;
    private String petName;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private Boolean isFinished;
    private String reservationType;
    private Boolean isCanceled;
    private Integer groomingFee;
    private Integer deliveryFee;
    private Integer monitoringFee;
    private Integer totalPayment;
    private String estimateDetail;

    private DesignerInfoDto designerInfo;
    private RequestDetailDto requestDetail;
}
