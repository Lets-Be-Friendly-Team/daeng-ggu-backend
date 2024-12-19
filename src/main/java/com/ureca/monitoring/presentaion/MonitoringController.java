package com.ureca.monitoring.presentaion;

import com.ureca.common.application.AuthService;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.monitoring.application.MonitoringService;
import com.ureca.monitoring.presentaion.dto.DesignerInfoDto;
import com.ureca.monitoring.presentaion.dto.ProcessStatusDto;
import com.ureca.monitoring.presentaion.dto.StreamingInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daengggu")
@RequiredArgsConstructor
@Tag(name = "Common API In Monitoring", description = "모니터링에서 공통으로 활용하는 API")
public class MonitoringController {

    private final MonitoringService monitoringService;
    private final AuthService authService;

    /**
     * 특정 예약에 대한 프로세스를 생성합니다.
     *
     * @param reservationId 예약 ID
     * @return 프로세스 생성 결과를 포함하는 ResponseDto
     */
    @PostMapping("/reservations/{reservationId}/processes/create")
    @Operation(summary = "버튼을 누르면 예약 프로세스 생성 (상태 변경)", description = "특정 예약을 선택하여 프로세스를 생성하는 API.")
    public ResponseDto<ProcessStatusDto> createProcess(
            @PathVariable Long reservationId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
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
    public ResponseDto<ProcessStatusDto> getProcessStatus(
            @PathVariable Long reservationId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("상태 조회 성공", monitoringService.getProcessStatus(reservationId));
    }

    /**
     * 보호자가 스트리밍 데이터를 조회하는 API.
     *
     * @param reservationId 예약 ID
     * @return 스트리밍 정보를 포함하는 ResponseDto
     */
    @GetMapping("/reservations/{reservationId}/streaming")
    @Operation(summary = "스트리밍 데이터 조회", description = "보호자는 가디언 또는 미용실의 스트리밍을 볼 수 있습니다.")
    public ResponseDto<StreamingInfoDto> getStreamingInfo(
            @PathVariable Long reservationId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS(
                "스트리밍 정보 조회 성공", monitoringService.getStreamingInfo(reservationId));
    }

    @GetMapping("/reservations/{reservationId}/designer-info")
    @Operation(summary = "해당 예약의 디자이너 정보 조회", description = "보호자는 디자이너의 정보를 볼 수 있습니다.")
    public ResponseDto<DesignerInfoDto> getDesignerInfoForMonitoring(
            @PathVariable Long reservationId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS(
                "디자이너 정보 조회 성공", monitoringService.getDesignerInfo(reservationId));
    }
}
