package com.ureca.reservation.presentation.dto;

import java.time.LocalDate;
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
    private String majorBreedCode;
    private String majorBreed;
    private String subBreedCode;
    private String subBreed;
    private String reservationType;
    private Boolean isFinished;
    private Boolean isCanceled;
    private LocalDate reservationDate;
    private String dayOfWeek;
    private String amPm;
    private Integer startTime;
    private Integer groomingFee;
    private Integer deliveryFee;
    private Integer monitoringFee;
    private Integer totalPayment;
    private String estimateDetail;

    private DesignerInfoDto designerInfo;
    private RequestDetailDto requestDetail;
}
