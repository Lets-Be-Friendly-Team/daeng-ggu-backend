package com.ureca.reservation.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.util.ValidationUtil;
import com.ureca.estimate.domain.Estimate;
import com.ureca.estimate.infrastructure.EstimateRepository;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.infrastructure.CommonCodeRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.reservation.config.PaymentServerConfig;
import com.ureca.reservation.domain.Reservation;
import com.ureca.reservation.infrastructure.ReservationRepository;
import com.ureca.reservation.presentation.dto.DesignerAvailableDatesResponseDto;
import com.ureca.reservation.presentation.dto.DesignerInfoDto;
import com.ureca.reservation.presentation.dto.EstimateReservationRequestDto;
import com.ureca.reservation.presentation.dto.PaymentRequestDto;
import com.ureca.reservation.presentation.dto.PaymentResponseDto;
import com.ureca.reservation.presentation.dto.RequestDetailDto;
import com.ureca.reservation.presentation.dto.ReservationHistoryResponseDto;
import com.ureca.reservation.presentation.dto.ReservationInfo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final DesignerRepository designerRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final EstimateRepository estimateRepository;
    private final PaymentServerConfig paymentServerConfig;
    private final RestTemplate restTemplate;

    public List<ReservationHistoryResponseDto> getReservationsByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ApiException(ErrorCode.USER_NOT_EXIST);
        }

        // TODO: 현재 로그인 중인 사용자와 찾으려는 예약정보의 customerId가 일치하는지 검증 필요 (시큐리티 적용 후 로그인된 유저 활용)

        List<Reservation> reservations = reservationRepository.findAllByCustomerId(customerId);
        if (reservations.isEmpty()) {
            throw new ApiException(ErrorCode.HISTORY_NOT_EXIST);
        }
        return reservations.stream()
                .map(
                        reservation ->
                                ReservationHistoryResponseDto.builder()
                                        .reservationId(reservation.getReservationId())
                                        .petName(reservation.getPet().getPetName())
                                        .reservationDate(reservation.getReservationDate())
                                        .startTime(reservation.getStartTime())
                                        .isFinished(reservation.getIsFinished())
                                        .reservationType(reservation.getReservationType())
                                        .isCanceled(reservation.getIsCanceled())
                                        .groomingFee(reservation.getGroomingFee().intValue())
                                        .deliveryFee(
                                                reservation.getDeliveryFee() != null
                                                        ? reservation.getDeliveryFee().intValue()
                                                        : null)
                                        .monitoringFee(
                                                reservation.getMonitoringFee() != null
                                                        ? reservation.getMonitoringFee().intValue()
                                                        : null)
                                        .totalPayment(reservation.getTotalPayment().intValue())
                                        .designerInfo(
                                                buildDesignerInfoDto(reservation.getDesigner()))
                                        .estimateDetail(
                                                reservation.getEstimate() != null
                                                        ? reservation
                                                                .getEstimate()
                                                                .getEstimate_detail()
                                                        : null)
                                        .requestDetail(buildRequestDetailDto(reservation))
                                        .build())
                .collect(Collectors.toList());
    }

    private DesignerInfoDto buildDesignerInfoDto(Designer designer) {
        return DesignerInfoDto.builder()
                .designerId(designer.getDesignerId())
                .designerName(designer.getDesignerName())
                .officialName(designer.getOfficialName())
                .designerImgUrl(designer.getDesignerImgUrl())
                .designerImgName(designer.getDesignerImgName())
                .address1(designer.getAddress1())
                .address2(designer.getAddress2())
                .detailAddress(designer.getDetailAddress())
                .introduction(designer.getIntroduction())
                .workExperience(designer.getWorkExperience())
                .businessNumber(designer.getBusinessNumber())
                .businessIsVerified(designer.getBusinessIsVerified())
                .build();
    }

    private RequestDetailDto buildRequestDetailDto(Reservation reservation) {
        if ("R2".equals(reservation.getReservationType()) && reservation.getRequest() != null) {
            // Auction 방식: Request 데이터 사용
            return RequestDetailDto.builder()
                    .desiredService(getCodeDesc(reservation.getRequest().getDesired_service_code()))
                    .lastGroomingDate(getCodeDesc(reservation.getRequest().getLast_grooming_date()))
                    .isDelivery(reservation.getRequest().getIs_delivery())
                    .desiredRegion(reservation.getRequest().getDesired_region())
                    .isMonitoring(reservation.getRequest().getIs_monitoringIncluded())
                    .additionalRequest(reservation.getRequest().getAdditional_request())
                    .build();
        }

        // Direct 방식: Reservation 데이터 사용
        return RequestDetailDto.builder()
                .desiredService(getCodeDesc(reservation.getDesiredService()))
                .lastGroomingDate(getCodeDesc(reservation.getLastGroomingDate()))
                .isDelivery(reservation.getIsDelivery())
                .desiredRegion(null) // Direct 예약은 지역 정보 없음
                .isMonitoring(reservation.getIsMonitoring())
                .additionalRequest(reservation.getAdditionalRequest())
                .build();
    }

    private String getCodeDesc(String code) {
        return commonCodeRepository.findByCodeId(code).getCodeDesc();
    }

    public List<DesignerAvailableDatesResponseDto> getAvailableDate(
            Long designerId, int year, int month) {

        // 조회하려는 디자이너가 데이터베이스에 존재하는 디자이너인지 검증
        if (!designerRepository.existsById(designerId)) {
            throw new ApiException(ErrorCode.USER_NOT_EXIST);
        }

        ValidationUtil.validateYearAndMonth(year, month);

        // 디자이너 예약 데이터 가져오기
        List<ReservationInfo> reservations =
                reservationRepository.findReservationsByDesignerAndMonth(designerId, year, month);

        // 예약 데이터를 날짜별로 그룹화
        Map<LocalDate, List<ReservationInfo>> reservationsByDate =
                reservations.stream()
                        .collect(Collectors.groupingBy(ReservationInfo::getReservationDate));

        List<DesignerAvailableDatesResponseDto> availableDates = new ArrayList<>();
        // TODO: 현재 시간을 기준으로 이전 날짜와 이전 시간은 리스트에 포함하지 않도록 처리 필요
        // 요청된 월의 모든 날짜를 순회하며 가능한 시간 계산
        for (int day = 1; day <= LocalDate.of(year, month, 1).lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);

            // 예약이 없는 경우 기본 전체 시간대 추가
            if (!reservationsByDate.containsKey(date)) {
                availableDates.add(
                        DesignerAvailableDatesResponseDto.builder()
                                .date(date)
                                .availableTimes(generateFullDayAvailableTimes())
                                .build());
            } else {
                // 예약이 있는 날짜의 가능한 시간 계산
                List<Integer> unavailableTimes =
                        reservationsByDate.get(date).stream()
                                .flatMap(
                                        reservation ->
                                                generateTimesFromRange(
                                                        reservation.getStartTime(),
                                                        reservation.getEndTime())
                                                        .stream())
                                .toList();

                List<Integer> availableTimes =
                        generateFullDayAvailableTimes().stream()
                                .filter(hour -> !unavailableTimes.contains(hour))
                                .collect(Collectors.toList());

                if (!availableTimes.isEmpty()) {
                    availableDates.add(
                            DesignerAvailableDatesResponseDto.builder()
                                    .date(date)
                                    .availableTimes(availableTimes)
                                    .build());
                }
            }
        }

        return availableDates;
    }

    private List<Integer> generateFullDayAvailableTimes() {
        // 하루의 기본 가능한 시간대 (9시~21시)
        List<Integer> times = new ArrayList<>();
        for (int hour = 9; hour <= 21; hour++) {
            times.add(hour);
        }
        return times;
    }

    private List<Integer> generateTimesFromRange(LocalTime startTime, LocalTime endTime) {
        // 특정 시간 범위에서 시간 리스트 생성
        List<Integer> times = new ArrayList<>();
        for (int hour = startTime.getHour(); hour < endTime.getHour(); hour++) {
            times.add(hour);
        }
        return times;
    }

    public Long estimateReservation(
            Long customerId, EstimateReservationRequestDto reservationRequestDto) {
        // 1. 예약 가능 여부에 대한 검증
        ValidationUtil.validateReservationTime(
                reservationRequestDto.getStartTime(), reservationRequestDto.getEndTime());
        ValidationUtil.validateAfterCurrentDateTime(
                reservationRequestDto.getReservationDate(), reservationRequestDto.getStartTime());

        // 2. 고객 및 요청 데이터 유효성 확인
        if (!customerRepository.existsById(customerId)) {
            throw new ApiException(ErrorCode.USER_NOT_EXIST);
        }

        Estimate estimate =
                estimateRepository
                        .findById(reservationRequestDto.getEstimateId())
                        .orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_EXIST));
        String customerEmail = customerRepository.findById(customerId).get().getEmail();

        if (estimate.getDesigner().getEmail().equals(customerEmail)) {
            throw new ApiException(ErrorCode.SAME_USER_RESERVE_DENIED);
        }

        // TODO: 예약 과정에서의 동시성 처리 문제 해결 필요

        // 3. 결제 서버에 요청
        PaymentRequestDto paymentRequestDto = buildPaymentRequest(reservationRequestDto);
        PaymentResponseDto paymentResponse = processPayment(paymentRequestDto);

        if (paymentResponse.getStatus().equals("FAILED")) {
            throw new ApiException(ErrorCode.PAYMENT_PROCESS_FAILED);
        }

        // 4. 예약 데이터 저장
        Reservation reservation = saveEstimateReservation(estimate, reservationRequestDto);

        // 5. 예약 성공 ID 반환
        return reservation.getReservationId();
    }

    private PaymentRequestDto buildPaymentRequest(
            EstimateReservationRequestDto reservationRequestDto) {
        return PaymentRequestDto.builder()
                .paymentKey(reservationRequestDto.getPaymentKey())
                .orderId(reservationRequestDto.getOrderId())
                .amount(reservationRequestDto.getTotalPayment())
                .build();
    }

    private Reservation saveEstimateReservation(
            Estimate estimate, EstimateReservationRequestDto reservationRequestDto) {
        Reservation reservation =
                Reservation.builder()
                        .request(estimate.getRequest())
                        .estimate(estimate)
                        .pet(estimate.getRequest().getPet())
                        .designer(estimate.getDesigner())
                        .reservationType("R2")
                        .isFinished(false)
                        .isCanceled(false)
                        .reservationDate(reservationRequestDto.getReservationDate())
                        .startTime(reservationRequestDto.getStartTime())
                        .endTime(reservationRequestDto.getEndTime())
                        .groomingFee(reservationRequestDto.getGroomingFee())
                        .deliveryFee(reservationRequestDto.getDeliveryFee())
                        .monitoringFee(reservationRequestDto.getMonitoringFee())
                        .totalPayment(reservationRequestDto.getTotalPayment())
                        .build();
        return reservationRepository.save(reservation);
    }

    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequestDto) {
        String paymentUrl = paymentServerConfig.getLocalPaymentServerUrl() + "/v1/toss/confirm";

        try {
            // 결제 서버에 요청
            PaymentResponseDto response =
                    restTemplate.postForObject(
                            paymentUrl, paymentRequestDto, PaymentResponseDto.class);

            // 응답 검증
            if (response == null) {
                throw new ApiException(ErrorCode.PAYMENT_SERVER_ERROR);
            }
            return response;

        } catch (Exception e) {
            // 결제 서버 예외 처리
            throw new ApiException(ErrorCode.PAYMENT_PROCESS_FAILED);
        }
    }
}
