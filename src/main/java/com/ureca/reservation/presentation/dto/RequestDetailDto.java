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
    private String desiredService; // 원하는 서비스
    private String lastGroomingDate; // 마지막 미용 시기
    private Boolean isDelivery; // 반려견 픽업 여부 (출장)
    private String desiredRegion; // 원하는 지역
    private Boolean isMonitoring; // 모니터링 서비스 여부
    private String additionalRequest; // 추가 요청 사항
}
