package com.ureca.monitoring.presentaion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationInfoForDesignerDto {
    private String customerPhone;
    private String customerName;

    private PetInfoDto petInfo;
    private ProcessStatusDto status;
}
