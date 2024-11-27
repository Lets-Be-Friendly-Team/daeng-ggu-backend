package com.ureca.reservation.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.reservation.domain.Reservation;
import com.ureca.reservation.infrastructure.ReservationRepository;
import com.ureca.reservation.presentation.dto.RequestDetailDto;
import com.ureca.reservation.presentation.dto.ReservationHistoryResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<ReservationHistoryResponseDto> getReservationsByCustomerId(Long customerId) {
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
                                        .requestDetail(buildRequestDetailDto(reservation))
                                        .build())
                .collect(Collectors.toList());
    }

    private RequestDetailDto buildRequestDetailDto(Reservation reservation) {
        if ("R2".equals(reservation.getReservationType()) && reservation.getRequest() != null) {
            // Auction 방식: Request 데이터 사용
            return RequestDetailDto.builder()
                    .desiredService(reservation.getRequest().getDesiredServiceCode())
                    .lastGroomingDate(reservation.getRequest().getLastGroomingDate())
                    .isDelivery(reservation.getRequest().getIsDelivery())
                    .desiredRegion(reservation.getRequest().getDesiredRegion())
                    .isMonitoring(reservation.getRequest().getIsMonitoringIncluded())
                    .additionalRequest(reservation.getRequest().getAdditionalRequest())
                    .build();
        }

        // Direct 방식: Reservation 데이터 사용
        return RequestDetailDto.builder()
                .desiredService(reservation.getDesiredService())
                .lastGroomingDate(reservation.getLastGroomingDate())
                .isDelivery(reservation.getIsDelivery())
                .desiredRegion(null) // Direct 예약은 지역 정보 없음
                .isMonitoring(reservation.getIsMonitoring())
                .additionalRequest(reservation.getAdditionalRequest())
                .build();
    }
}
