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
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.reservation.config.PaymentServerConfig;
import com.ureca.reservation.domain.Reservation;
import com.ureca.reservation.infrastructure.ReservationRepository;
import com.ureca.reservation.presentation.dto.DesignerAvailableDatesResponseDto;
import com.ureca.reservation.presentation.dto.DesignerInfoDto;
import com.ureca.reservation.presentation.dto.DirectReservationRequestDto;
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


/**
 * 예약 관련 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final DesignerRepository designerRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final EstimateRepository estimateRepository;
    private final PetRepository petRepository;
    private final PaymentServerConfig paymentServerConfig;
    private final RestTemplate restTemplate;

    /**
     * 고객 ID를 기반으로 예약 목록을 조회합니다.
     *
     * @param customerId 보호자의 고유 ID
     * @return 보호자의 예약 목록
     * @throws ApiException CUSTOMER_NOT_EXIST: 보호자가 존재하지 않을 경우
     *                      HISTORY_NOT_EXIST: 예약 기록이 없을 경우
     */
    public List<ReservationHistoryResponseDto> getReservationsByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ApiException(ErrorCode.CUSTOMER_NOT_EXIST);
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
                                                                .getEstimateDetail()
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
                    .desiredService(getCodeDesc(reservation.getRequest().getDesiredServiceCode()))
                    .lastGroomingDate(getCodeDesc(reservation.getRequest().getLastGroomingDate()))
                    .isDelivery(reservation.getRequest().getIsDelivery())
                    .desiredRegion(reservation.getRequest().getDesiredRegion())
                    .isMonitoring(reservation.getRequest().getIsMonitoringIncluded())
                    .additionalRequest(reservation.getRequest().getAdditionalRequest())
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

    /**
     * 특정 디자이너의 예약 가능 날짜를 조회합니다.
     *
     * @param designerId 디자이너의 고유 ID
     * @param year 예약 가능 날짜 조회 연도
     * @param month 예약 가능 날짜 조회 월
     * @return 디자이너의 예약 가능 날짜 리스트
     * @throws ApiException DESIGNER_NOT_EXIST: 디자이너가 존재하지 않을 경우
     */
    public List<DesignerAvailableDatesResponseDto> getAvailableDate(
            Long designerId, int year, int month) {

        // TODO: 디자이너 영업 요일 데이터 필터링 추가

        // 조회하려는 디자이너가 데이터베이스에 존재하는 디자이너인지 검증
        if (!designerRepository.existsById(designerId)) {
            throw new ApiException(ErrorCode.DESIGNER_NOT_EXIST);
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
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 요청된 월의 모든 날짜를 순회하며 가능한 시간 계산
        for (int day = 1; day <= LocalDate.of(year, month, 1).lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);

            // 과거 날짜는 무시
            if (date.isBefore(today)) {
                continue;
            }

            // 예약이 없는 경우 기본 전체 시간대 추가
            if (!reservationsByDate.containsKey(date)) {
                // 현재 날짜일 경우 현재 시간 이후의 시간대만 포함
                List<Integer> availableTimes = generateFullDayAvailableTimes();
                if (date.isEqual(today)) {
                    availableTimes =
                            availableTimes.stream()
                                    .filter(hour -> hour >= now.getHour())
                                    .collect(Collectors.toList());
                }

                if (!availableTimes.isEmpty()) {
                    availableDates.add(
                            DesignerAvailableDatesResponseDto.builder()
                                    .date(date)
                                    .availableTimes(availableTimes)
                                    .build());
                }
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

                // 현재 날짜일 경우 현재 시간 이후의 시간대만 포함
                if (date.isEqual(today)) {
                    availableTimes =
                            availableTimes.stream()
                                    .filter(hour -> hour >= now.getHour())
                                    .collect(Collectors.toList());
                }

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

    /**
     * 입찰 예약을 생성합니다.
     *
     * @param customerId 보호자의 고유 ID
     * @param estimateReservationRequestDto 예약 요청 데이터
     * @return 생성된 예약 ID
     * @throws ApiException CUSTOMER_NOT_EXIST: 보호자가 존재하지 않을 경우
     *                      DATA_NOT_EXIST: 견적 데이터가 없을 경우
     *                      PAYMENT_PROCESS_FAILED: 결제 실패 시
     */
    public Long estimateReservation(
            Long customerId, EstimateReservationRequestDto estimateReservationRequestDto) {
        // 1. 예약 가능 여부에 대한 검증
        ValidationUtil.validateReservationTime(
                estimateReservationRequestDto.getStartTime(),
                estimateReservationRequestDto.getEndTime());
        ValidationUtil.validateAfterCurrentDateTime(
                estimateReservationRequestDto.getReservationDate(),
                estimateReservationRequestDto.getStartTime());

        // 2. 보호자 및 요청 데이터 유효성 확인
        if (!customerRepository.existsById(customerId)) {
            throw new ApiException(ErrorCode.CUSTOMER_NOT_EXIST);
        }

        Estimate estimate =
                estimateRepository
                        .findById(estimateReservationRequestDto.getEstimateId())
                        .orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_EXIST));
        String customerEmail = customerRepository.findById(customerId).get().getEmail();

        if (estimate.getDesigner().getEmail().equals(customerEmail)) {
            throw new ApiException(ErrorCode.SAME_USER_RESERVE_DENIED);
        }

        // TODO: 예약 과정에서의 동시성 처리 문제 해결 필요

        // 3. 결제 서버에 요청
        PaymentRequestDto paymentRequestDto = buildPaymentRequest(estimateReservationRequestDto);
        PaymentResponseDto paymentResponse = processPayment(paymentRequestDto);

        if (paymentResponse.getStatus().equals("FAILED")) {
            throw new ApiException(ErrorCode.PAYMENT_PROCESS_FAILED);
        }

        // 4. 예약 데이터 저장
        Reservation reservation = saveEstimateReservation(estimate, estimateReservationRequestDto);

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

    /**
     * 직접 예약을 생성합니다.
     *
     * @param customerId 보호자의 고유 ID
     * @param directReservationRequestDto 예약 요청 데이터
     * @return 생성된 예약 ID
     * @throws ApiException CUSTOMER_NOT_EXIST: 보호자 존재하지 않을 경우
     *                      PAYMENT_PROCESS_FAILED: 결제 실패 시
     */
    public Long directReservation(
            Long customerId, DirectReservationRequestDto directReservationRequestDto) {
        // 1. 예약 가능 여부 검증
        ValidationUtil.validateReservationTime(
                directReservationRequestDto.getStartTime(),
                directReservationRequestDto.getEndTime());
        ValidationUtil.validateAfterCurrentDateTime(
                directReservationRequestDto.getReservationDate(),
                directReservationRequestDto.getStartTime());

        // TODO: 서비스별 소요 시간 반영 end time 생성 (디자이너의 데이터에서 가져와서 연산 수행)

        // 2. 보호자 및 요청 데이터 유효성 확인
        if (!customerRepository.existsById(customerId)) {
            throw new ApiException(ErrorCode.CUSTOMER_NOT_EXIST);
        }

        // 3. 결제 서버에 요청
        PaymentRequestDto paymentRequestDto = buildPaymentRequest(directReservationRequestDto);
        PaymentResponseDto paymentResponse = processPayment(paymentRequestDto);

        if ("FAILED".equals(paymentResponse.getStatus())) {
            throw new ApiException(ErrorCode.PAYMENT_PROCESS_FAILED);
        }

        // 4. 예약 데이터 저장
        Reservation reservation = saveDirectReservation(directReservationRequestDto);

        // 5. 예약 성공 ID 반환
        return reservation.getReservationId();
    }

    private PaymentRequestDto buildPaymentRequest(DirectReservationRequestDto directReservationRequestDto) {
        return PaymentRequestDto.builder()
            .paymentKey(directReservationRequestDto.getPaymentKey())
            .orderId(directReservationRequestDto.getOrderId())
            .amount(directReservationRequestDto.getAmount())
            .build();
    }

    private Reservation saveDirectReservation(DirectReservationRequestDto directReservationRequestDto) {
        return reservationRepository.save(
            Reservation.builder()
                .pet(
                    petRepository
                        .findById(directReservationRequestDto.getPetId())
                        .orElseThrow(() -> new ApiException(ErrorCode.PET_NOT_EXIST)))
                .designer(
                    designerRepository
                        .findById(directReservationRequestDto.getDesignerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.DESIGNER_NOT_EXIST)))
                .reservationType("R1") // Direct 예약
                .isFinished(false)
                .isCanceled(false)
                .reservationDate(directReservationRequestDto.getReservationDate())
                .startTime(directReservationRequestDto.getStartTime())
                .endTime(directReservationRequestDto.getEndTime())
                .groomingFee(directReservationRequestDto.getGroomingFee())
                .deliveryFee(directReservationRequestDto.getDeliveryFee())
                .monitoringFee(directReservationRequestDto.getMonitoringFee())
                .totalPayment(directReservationRequestDto.getTotalPayment())
                .desiredService(directReservationRequestDto.getDesiredService())
                .lastGroomingDate(directReservationRequestDto.getLastGroomingDate())
                .isDelivery(directReservationRequestDto.getIsDelivery())
                .isMonitoring(directReservationRequestDto.getIsMonitoring())
                .additionalRequest(directReservationRequestDto.getAdditionalRequest())
                .build());
    }


    // 결제 요청
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
