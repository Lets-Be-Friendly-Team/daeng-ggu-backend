package com.ureca.reservation.presentation.dto;

import java.math.BigDecimal;
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
public class PaymentRequestDto {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
