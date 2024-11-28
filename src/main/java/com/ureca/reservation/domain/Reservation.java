package com.ureca.reservation.domain;

import com.ureca.estimate.domain.Estimate;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Pet;
import com.ureca.request.domain.Request;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId; // 예약 ID

    // 요청서 연관관계 (OneToOne, NULL 가능)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestId", nullable = true)
    private Request request;

    // 견적서 연관관계 (OneToOne, NULL 가능)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimateId", nullable = true)
    private Estimate estimate;

    // 예약과 반려견 연관관계 (ManyToOne, NULL 불가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petId", nullable = false)
    private Pet pet;

    // 예약과 디자이너 연관관계 (ManyToOne, NULL 불가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designerId", nullable = false)
    private Designer designer;

    @Column(name = "reservationType", nullable = false, length = 20)
    private String reservationType; // 예약 유형 (commonCode -> "R1"(DIRECT), "R2"(AUCTION))

    @Column(name = "isFinished", nullable = false)
    private Boolean isFinished; // 예약 완료 여부

    @Column(name = "isCanceled", nullable = false)
    private Boolean isCanceled; // 예약 취소 여부

    @Column(name = "reservationDate", nullable = false)
    private LocalDate reservationDate; // 예약 일자

    @Column(name = "startTime", nullable = false)
    private LocalTime startTime; // 시작 시간

    @Column(name = "endTime", nullable = false)
    private LocalTime endTime; // 종료 시간

    @Column(name = "groomingFee", nullable = false, precision = 10, scale = 2)
    private BigDecimal groomingFee; // 미용비

    @Column(name = "deliveryFee", precision = 10, scale = 2)
    private BigDecimal deliveryFee; // 배송비 (NULL 가능)

    @Column(name = "monitoringFee", precision = 10, scale = 2)
    private BigDecimal monitoringFee; // 모니터링비 (NULL 가능)

    @Column(name = "totalPayment", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPayment; // 총 결제 금액

    @Column(name = "desiredService", length = 20)
    private String desiredService; // 원하는 서비스 (NULL 가능) (commonCode -> S)

    @Column(name = "lastGroomingDate", length = 20)
    private String lastGroomingDate; // 직전 미용 날짜 (NULL 가능) (commonCode -> L)

    @Column(name = "isDelivery")
    private Boolean isDelivery; // 픽업 여부 (NULL 가능)

    @Column(name = "isMonitoring")
    private Boolean isMonitoring; // 모니터링 여부 (NULL 가능)

    @Column(name = "additionalRequest", length = 100)
    private String additionalRequest; // 추가 요청 사항 (NULL 가능)
}
