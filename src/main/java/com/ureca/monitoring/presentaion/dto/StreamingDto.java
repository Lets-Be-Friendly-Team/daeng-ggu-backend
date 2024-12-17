package com.ureca.monitoring.presentaion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StreamingDto {
    private Long reservationId;
    private String streamUrl;
    private String channelARN;
    private ProcessStatusDto statusDto;
}
