package com.ureca.reservation.presentation.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderKeysAndAmountDto {
    private String customerKey;
    private String orderId;
    private BigDecimal amount;
}
