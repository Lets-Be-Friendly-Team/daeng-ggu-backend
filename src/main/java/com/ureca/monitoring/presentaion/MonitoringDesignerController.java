package com.ureca.monitoring.presentaion;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.monitoring.application.MonitoringService;
import com.ureca.monitoring.presentaion.dto.ProcessStatusDto;
import com.ureca.monitoring.presentaion.dto.ReservationInfoForDesignerDto;
import com.ureca.monitoring.presentaion.dto.StreamingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daengggu/designer")
@RequiredArgsConstructor
@Tag(name = "Designer API In Monitoring", description = "모니터링에서 디자이너 관련 api")
public class MonitoringDesignerController {

    private final MonitoringService monitoringService;

    /**
     * 미용실이 시작 전 고객 정보를 확인하는 API.
     *
     * @param reservationId 예약 고유 ID
     * @return 고객 및 반려견 정보 + 진행 상태 DTO
     */
    @GetMapping("/process/{reservationId}/info")
    @Operation(summary = "시작 전 고객 정보 조회", description = "미용 시작 전 고객과 반려견 정보를 조회합니다.")
    public ResponseDto<ReservationInfoForDesignerDto> getProcessInfo(
            @PathVariable Long reservationId) {

        return ResponseUtil.SUCCESS(
                "고객 정보 조회 성공", monitoringService.getReservationInfo(reservationId));
    }

    /**
     * 미용실이 스트리밍 시작을 위한 API.
     *
     * @return 스트리밍 URL, 스트림 키 + 진행 상태 DTO
     */
    @PostMapping("/process/{reservationId}/start")
    @Operation(summary = "스트리밍 시작 (상태 변경)", description = "미용실이 스트리밍을 시작합니다.")
    public ResponseDto<StreamingDto> startStreaming(@PathVariable Long reservationId) {

        return ResponseUtil.SUCCESS(
                "고객 정보 조회 성공", monitoringService.designerStartStreaming(reservationId));
    }

    /**
     * 미용실이 스트리밍 종료를 위한 API.
     *
     * @return 진행 상태 DTO
     */
    @PostMapping("/process/{reservationId}/end")
    @Operation(summary = "스트리밍 종료 (상태 변경)", description = "미용실이 스트리밍을 종료합니다.")
    public ResponseDto<ProcessStatusDto> endProcess(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "스트리밍 종료", monitoringService.designerEndStreaming(reservationId));
    }
}
