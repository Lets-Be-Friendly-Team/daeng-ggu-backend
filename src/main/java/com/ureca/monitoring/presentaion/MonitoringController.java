package com.ureca.monitoring.presentaion;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.monitoring.application.MonitoringService;
import com.ureca.monitoring.presentaion.dto.ProcessStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daengggu")
@RequiredArgsConstructor
@Tag(name = "Status API In Monitoring", description = "모니터링에서 프로세스 상태 관련 api")
public class MonitoringController {

    private final MonitoringService monitoringService;

    /**
     * 특정 예약에 대한 프로세스를 생성합니다.
     *
     * @param reservationId 예약 ID
     * @return 프로세스 생성 결과를 포함하는 ResponseDto
     */
    @PostMapping("/reservations/{reservationId}/processes/create")
    @Operation(summary = "버튼을 누르면 예약 프로세스 생성 (상태 변경)", description = "특정 예약을 선택하여 프로세스를 생성하는 API.")
    public ResponseDto<ProcessStatusDto> createProcess(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS("프로세스 생성 성공", monitoringService.createProcess(reservationId));
    }

    /**
     * 특정 예약의 현재 프로세스 상태를 조회합니다.
     *
     * @param reservationId 예약 ID
     * @return 프로세스 상태를 포함하는 ResponseDto
     */
    @GetMapping("/process/{reservationId}/status")
    @Operation(summary = "모니터링 상태 조회", description = "모니터링의 상태를 조회하는 API")
    public ResponseDto<ProcessStatusDto> getProcessStatus(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS("상태 조회 성공", monitoringService.getProcessStatus(reservationId));
    }
}
