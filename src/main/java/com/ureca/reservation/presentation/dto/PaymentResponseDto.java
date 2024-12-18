package com.ureca.reservation.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentResponseDto {
    private String paymentKey; // 결제 식별 키
    private String orderId; // 주문 번호
    private String status; // 결제 상태 (DONE, FAILED 등)
    private BigDecimal totalAmount; // 결제 금액
    private LocalDateTime approvedAt; // 결제 승인 시간
    private String receiptUrl; // 영수증 URL (선택)
    private String method; // 결제 수단 (선택)
    private String failure; // 실패 이유 (선택)
}
