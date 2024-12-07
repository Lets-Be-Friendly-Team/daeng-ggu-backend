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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("daengggu")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("reservations")
    public ResponseDto<List<ReservationHistoryResponseDto>> getReservationList(
            @RequestParam Long customerId) {
        log.info("GET /daengggu/reservation/list - customerId: {}", customerId);
        return ResponseUtil.SUCCESS(
                "예약 목록 조회 성공", reservationService.getReservationsByCustomerId(customerId));
    }

    @GetMapping("reservation/designer/{designerId}/availability")
    public ResponseDto<List<DesignerAvailableDatesResponseDto>> getDesignerAvailability(
            @PathVariable Long designerId, @RequestParam int year, @RequestParam int month) {
        return ResponseUtil.SUCCESS(
                "예약 가능 시간 조회 성공", reservationService.getAvailableDate(designerId, year, month));
    }

    @PostMapping("reservation/estimate")
    public ResponseDto<Long> createEstimateReservation(
            @RequestParam Long customerId,
            @RequestBody EstimateReservationRequestDto estimateReservationRequestDto) {
        log.info(
                "POST /daengggu/reservation/estimate - request: {}", estimateReservationRequestDto);

        return ResponseUtil.SUCCESS(
                "예약 생성 성공",
                reservationService.estimateReservation(customerId, estimateReservationRequestDto));
    }

    @PostMapping("reservation/direct")
    public ResponseDto<Long> createDirectReservation(
            @RequestParam Long customerId,
            @RequestBody DirectReservationRequestDto directReservationRequestDto) {
        log.info("POST /daengggu/reservation/direct - request: {}", directReservationRequestDto);

        return ResponseUtil.SUCCESS(
                "예약 생성 성공",
                reservationService.directReservation(customerId, directReservationRequestDto));
    }
}
