package com.ureca.reservation.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.reservation.application.ReservationService;
import com.ureca.reservation.presentation.dto.ReservationHistoryResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("daengggu/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/list")
    public ResponseDto<List<ReservationHistoryResponseDto>> getReservationList(
            @RequestParam Long customerId) {
        log.info("GET /daengggu/reservation/list - customerId: {}", customerId);
        return ResponseUtil.SUCCESS(
                "예약 목록 조회 성공", reservationService.getReservationsByCustomerId(customerId));
    }
}
