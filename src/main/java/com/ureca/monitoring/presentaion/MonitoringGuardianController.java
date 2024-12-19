package com.ureca.monitoring.presentaion;

import com.ureca.common.application.AuthService;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.monitoring.application.MonitoringService;
import com.ureca.monitoring.presentaion.dto.ProcessStatusDto;
import com.ureca.monitoring.presentaion.dto.ReservationInfoForGuardianDto;
import com.ureca.monitoring.presentaion.dto.StreamingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daengggu/guardian")
@RequiredArgsConstructor
@Tag(name = "Guardian API In Monitoring", description = "모니터링에서 배달기사 관련 API")
public class MonitoringGuardianController {

    private final MonitoringService monitoringService;
    private final AuthService authService;

    /**
     * 배달기사가 전체 예약 리스트를 조회하는 API.
     *
     * @return 예약 리스트
     */
    @GetMapping("/reservations")
    @Operation(summary = "예약 리스트 조회", description = "배달기사가 전체 예약 리스트를 조회하는 API.")
    public ResponseDto<List<ReservationInfoForGuardianDto>> getReservationList(
            HttpServletRequest request, HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS(
                "예약 리스트 조회 성공", monitoringService.getUpcomingDeliveryReservations());
    }

    /**
     * 배달기사가 단일 예약 정보를 조회하는 API.
     *
     * @return 예약 정보
     */
    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "특정 예약 정보 조회", description = "배달기사가 예약 한개의 정보를 조회하는 API.")
    public ResponseDto<ReservationInfoForGuardianDto> getReservationInfo(
            @PathVariable Long reservationId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS(
                "예약 정보 조회 성공", monitoringService.getGuardianReservationInfo(reservationId));
    }

    /**
     * 배달기사가 미용실로 배송 시작을 위한 API.
     *
     * @return 스트리밍 DTO
     */
    @PostMapping("/process/{reservationId}/start-delivery-to-shop")
    @Operation(summary = "배송 시작을 눌러 미용실로 배송 시작 (상태 변경)", description = "배달기사가 미용실로 배송 시작을 위한 API.")
    public ResponseDto<StreamingDto> startDeliveryToShop(
            @PathVariable Long reservationId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("배송 시작", monitoringService.startDeliveryToShop(reservationId));
    }

    /**
     * 배달기사가 미용실 도착 버튼을 누를 때 상태 변경 API.
     *
     * @return 진행 상태 DTO
     */
    @PostMapping("/process/{reservationId}/arrive-at-shop")
    @Operation(
            summary = "도착 버튼을 눌러 미용실 도착 (상태 변경)",
            description = "배달기사가 미용실 도착 버튼을 누를 때 상태 변경 API.")
    public ResponseDto<ProcessStatusDto> arriveAtShop(
            @PathVariable Long reservationId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("미용실 도착", monitoringService.arriveAtShop(reservationId));
    }

    /**
     * 배달기사가 집으로 배송 시작을 위한 API.
     *
     * @return 스트리밍 DTO
     */
    @PostMapping("/process/{reservationId}/start-delivery-to-home")
    @Operation(summary = "배송 시작을 눌러 집으로 배송 시작 (상태 변경)", description = "배달기사가 집으로 배송 시작을 위한 API.")
    public ResponseDto<StreamingDto> startDeliveryToHome(
            @PathVariable Long reservationId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS(
                "집으로 배송 시작", monitoringService.startDeliveryToHome(reservationId));
    }

    /**
     * 배달기사가 집 도착 버튼을 누를 때 상태 변경 API.
     *
     * @return 진행 상태 DTO
     */
    @PostMapping("/process/{reservationId}/arrive-at-home")
    @Operation(summary = "배송완료를 눌러 집 도착 (상태 변경)", description = "배달기사가 집 도착 버튼을 누를 때 상태 변경 API.")
    public ResponseDto<ProcessStatusDto> arriveAtHome(
            @PathVariable Long reservationId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("집 도착", monitoringService.arriveAtHome(reservationId));
    }
}
