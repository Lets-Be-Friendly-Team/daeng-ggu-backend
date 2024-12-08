package com.ureca.reservation.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.reservation.application.ReservationService;
import com.ureca.reservation.presentation.dto.DesignerAvailableDatesResponseDto;
import com.ureca.reservation.presentation.dto.DirectReservationRequestDto;
import com.ureca.reservation.presentation.dto.EstimateReservationRequestDto;
import com.ureca.reservation.presentation.dto.ReservationHistoryResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ReservationController
 * 예약 관련 요청을 처리하는 REST API 컨트롤러
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("daengggu")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 고객 ID를 기반으로 예약 목록을 조회합니다.
     *
     * @param customerId 고객의 고유 ID
     * @return 예약 목록 (List<ReservationHistoryResponseDto>)
     */
    // TODO: 소셜로그인 적용 후 customerId 응답 처리 (로그인된 보호자 활용)
    @GetMapping("reservations")
    public ResponseDto<List<ReservationHistoryResponseDto>> getReservationList(
            @RequestParam Long customerId) {
        return ResponseUtil.SUCCESS(
                "예약 목록 조회 성공", reservationService.getReservationsByCustomerId(customerId));
    }

    /**
     * 특정 디자이너의 예약 가능 날짜를 조회합니다.
     *
     * @param designerId 디자이너의 고유 ID
     * @param year 예약 가능 날짜 조회의 기준 연도
     * @param month 예약 가능 날짜 조회의 기준 월
     * @return 예약 가능 날짜, 시간 리스트 (List<DesignerAvailableDatesResponseDto>)
     */
    @GetMapping("reservation/designer/{designerId}/availability")
    public ResponseDto<List<DesignerAvailableDatesResponseDto>> getDesignerAvailability(
            @PathVariable Long designerId, @RequestParam int year, @RequestParam int month) {
        return ResponseUtil.SUCCESS(
                "예약 가능 시간 조회 성공", reservationService.getAvailableDate(designerId, year, month));
    }

    /**
     * 입찰 예약을 생성합니다.
     *
     * @param customerId 고객의 고유 ID
     * @param estimateReservationRequestDto 예약에 대한 세부 정보 (EstimateReservationRequestDto)
     * @return 생성된 예약의 고유 ID (Long)
     */
    @PostMapping("reservation/estimate")
    public ResponseDto<Long> createEstimateReservation(
            @RequestParam Long customerId,
            @RequestBody EstimateReservationRequestDto estimateReservationRequestDto) {
        return ResponseUtil.SUCCESS(
                "예약 생성 성공",
                reservationService.estimateReservation(customerId, estimateReservationRequestDto));
    }

    /**
     * 직접 예약을 생성합니다.
     *
     * @param customerId 고객의 고유 ID
     * @param directReservationRequestDto 예약에 대한 세부 정보 (DirectReservationRequestDto)
     * @return 생성된 예약의 고유 ID (Long)
     */
    @PostMapping("reservation/direct")
    public ResponseDto<Long> createDirectReservation(
            @RequestParam Long customerId,
            @RequestBody DirectReservationRequestDto directReservationRequestDto) {
        return ResponseUtil.SUCCESS(
                "예약 생성 성공",
                reservationService.directReservation(customerId, directReservationRequestDto));
    }
}
