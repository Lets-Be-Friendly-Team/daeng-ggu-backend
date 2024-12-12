package com.ureca.reservation.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.reservation.application.ReservationService;
import com.ureca.reservation.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/** ReservationController 예약 관련 요청을 처리하는 REST API 컨트롤러 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("daengggu")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 고객 ID를 기반으로 예약 목록 조회
     *
     * @param customerId 고객의 고유 ID
     * @return 예약 내역 리스트
     */
    @GetMapping("/reservations")
    @Operation(summary = "예약 목록 조회", description = "[RSV1000] 고객의 예약 내역을 조회합니다.")
    public ResponseDto<List<ReservationHistoryResponseDto>> getReservationList(
            @RequestParam Long customerId) {
        return ResponseUtil.SUCCESS(
                "예약 목록 조회 성공", reservationService.getReservationsByCustomerId(customerId));
    }

    /**
     * 디자이너 ID를 기반으로 예약 내역 조회
     *
     * @param designerId 디자이너의 고유 ID
     * @return 예약 내역 리스트
     */
    @GetMapping("designer/{designerId}/daengggu/reservations")
    @Operation(summary = "디자이너 예약 내역 조회", description = "[DRSV1000] 디자이너의 예약 내역을 조회합니다.")
    public ResponseDto<List<ReservationHistoryResponseDto>> getReservationListByDesigner(
            @PathVariable Long designerId) {
        return ResponseUtil.SUCCESS(
                "예약 목록 조회 성공", reservationService.getReservationsByDesignerId(designerId));
    }

    /**
     * 디자이너의 예약 가능 날짜 조회
     *
     * @param designerId 디자이너의 고유 ID
     * @param year 조회 기준 연도
     * @param month 조회 기준 월
     * @return 예약 가능 날짜 및 시간 리스트
     */
    @GetMapping("reservation/designer/{designerId}/availability")
    @Operation(summary = "디자이너 예약 가능 시간 조회", description = "[REQ1100] 디자이너의 예약 가능 날짜 및 시간을 조회합니다.")
    public ResponseDto<List<DesignerAvailableDatesResponseDto>> getDesignerAvailability(
            @PathVariable Long designerId, @RequestParam int year, @RequestParam int month) {
        return ResponseUtil.SUCCESS(
                "예약 가능 시간 조회 성공", reservationService.getAvailableDate(designerId, year, month));
    }

    /**
     * customerKey, orderId 반환 API
     *
     * @param customerId 고객의 고유 ID
     * @return customerKey, orderId 정보
     */
    @GetMapping("reservation/payment/keys")
    @Operation(summary = "결제 정보 반환", description = "[결제 위젯] customerKey와 orderId 반환 API")
    public ResponseDto<OrderKeysDto> getCustomerKey(@RequestParam Long customerId) {
        return ResponseUtil.SUCCESS(
                "결제 정보 반환 성공", reservationService.getCustomerKeyAndOrderId(customerId));
    }

    /**
     * customerKey, orderId, amount 저장 API
     *
     * @param customerId 고객의 고유 ID
     * @param orderKeysAndAmountDto 결제 정보
     * @return 처리 상태 메시지
     */
    @PostMapping("reservation/payment/keys")
    @Operation(summary = "결제 데이터 저장", description = "[결제 위젯] 결제 데이터를 저장합니다.")
    public ResponseDto<Void> savePaymentData(
            @RequestParam Long customerId,
            @RequestBody OrderKeysAndAmountDto orderKeysAndAmountDto) {
        reservationService.saveOrderKeysAndAmount(customerId, orderKeysAndAmountDto);
        return ResponseUtil.SUCCESS("결제 데이터 저장 성공", null);
    }

    /**
     * 입찰 예약 생성 API
     *
     * @param customerId 고객의 고유 ID
     * @param estimateReservationRequestDto 예약 세부 정보
     * @return 생성된 예약 ID
     */
    @PostMapping("reservation/estimate")
    @Operation(summary = "입찰 예약 생성", description = "[REQ2300] 견적서 기반 입찰 예약을 생성합니다.")
    public ResponseDto<Long> createEstimateReservation(
            @RequestParam Long customerId,
            @RequestBody EstimateReservationRequestDto estimateReservationRequestDto) {
        return ResponseUtil.SUCCESS(
                "예약 생성 성공",
                reservationService.estimateReservation(customerId, estimateReservationRequestDto));
    }

    /**
     * 직접 예약 생성 API
     *
     * @param customerId 고객의 고유 ID
     * @param directReservationRequestDto 예약 세부 정보
     * @return 생성된 예약 ID
     */
    @PostMapping("reservation/direct")
    @Operation(summary = "직접 예약 생성", description = "[DMYP1000] 디자이너 프로필에서 직접 예약을 생성합니다.")
    public ResponseDto<Long> createDirectReservation(
            @RequestParam Long customerId,
            @RequestBody DirectReservationRequestDto directReservationRequestDto) {
        return ResponseUtil.SUCCESS(
                "예약 생성 성공",
                reservationService.directReservation(customerId, directReservationRequestDto));
    }

    /**
     * 예약 취소 처리
     *
     * @param reservationId 예약 고유 ID
     * @return 환불 금액
     */
    @GetMapping("/reservation/{reservationId}/cancel")
    @Operation(summary = "예약 취소", description = "[RSV1000] 예약 취소 상태로 변경합니다.")
    public ResponseDto<Integer> cancelReservation(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "예약 취소 성공", reservationService.cancelReservation(reservationId));
    }
}
