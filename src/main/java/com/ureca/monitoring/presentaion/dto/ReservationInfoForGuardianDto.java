package com.ureca.monitoring.presentaion.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationInfoForGuardianDto {
    private Long reservationId;
    private LocalDate reservationDate;
    private Integer startTime;
    private Boolean isFinished;
    private Long processId;

    private String customerAddress;
    private String shopAddress;

    private PetInfoDto petInfo;
}
