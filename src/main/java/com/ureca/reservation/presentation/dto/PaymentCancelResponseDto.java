package com.ureca.reservation.presentation.dto;

import java.math.BigDecimal;
import java.util.List;
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
public class PaymentCancelResponseDto {
    private String paymentKey;
    private String orderId;
    private String status;

    private List<CancelDetail> cancels; // 배열로 매핑

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class CancelDetail {
        private String transactionKey;
        private String cancelReason;
        private String canceledAt;
        private BigDecimal cancelAmount;
        private String cancelStatus;
    }
}
