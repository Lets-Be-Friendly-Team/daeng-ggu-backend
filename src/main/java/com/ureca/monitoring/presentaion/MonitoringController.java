package com.ureca.monitoring.presentaion;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.monitoring.presentaion.dto.ProcessStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daengggu")
@RequiredArgsConstructor
@Tag(name = "Monitoring Status API", description = "모니터링에서 프로세스 상태 관련 api")
public class MonitoringController {

    @GetMapping("/process/{reservationId}/status")
    @Operation(summary = "모니터링 상태 조회", description = "모니터링의 상태를 조회하는 api")
    public ResponseDto<ProcessStatusDto> endProcess(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "상태 조회 성공",
                ProcessStatusDto.builder()
                        .isDelivery(true)
                        .processNum(5)
                        .processStatus("WAITING_FOR_DELIVERY")
                        .processMessage("미용 종료 후 배송 대기")
                        .build());
    }
}
