package com.ureca.reservation.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDetailDto {
    private String desiredService;
    private String lastGroomingDate;
    private Boolean isDelivery;
    private String desiredRegion;
    private Boolean isMonitoring;
    private String additionalRequest;
}
