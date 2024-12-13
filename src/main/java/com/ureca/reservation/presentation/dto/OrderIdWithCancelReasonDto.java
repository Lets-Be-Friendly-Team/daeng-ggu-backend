package com.ureca.reservation.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderIdWithCancelReasonDto {
    private String orderId;
    private String cancelReason;
}
