package com.ureca.monitoring.presentaion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessStatusDto {
    private Boolean isDelivery;
    private int processNum;
    private String processStatus;
    private String processMessage;
}
