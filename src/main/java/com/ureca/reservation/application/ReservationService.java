package com.ureca.reservation.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.infrastructure.CommonCodeRepository;
import com.ureca.reservation.domain.Reservation;
import com.ureca.reservation.infrastructure.ReservationRepository;
import com.ureca.reservation.presentation.dto.DesignerInfoDto;
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
    private final CommonCodeRepository commonCodeRepository;

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
}
