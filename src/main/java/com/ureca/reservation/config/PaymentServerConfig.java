package com.ureca.reservation.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class PaymentServerConfig {

    @Value("${payment.server.url}")
    private String paymentServerUrl;

    @Value("${payment.local-server.url}")
    private String localPaymentServerUrl;
}
