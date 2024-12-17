package com.ureca.monitoring.presentaion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StreamingInfoDto {
    private Long reservationId;
    private String streamUrl;
}
