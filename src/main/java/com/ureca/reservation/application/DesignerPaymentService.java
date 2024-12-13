package com.ureca.reservation.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.reservation.config.PaymentServerConfig;
import com.ureca.reservation.presentation.dto.PaymentRequestDto;
import com.ureca.reservation.presentation.dto.PaymentResponseDto;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DesignerPaymentService {

    private final DesignerRepository designerRepository;
    private final RestTemplate restTemplate;
    private final PaymentServerConfig paymentServerConfig;

    /**
     * 디자이너 결제를 처리합니다.
     *
     * @param designerId 디자이너의 고유 ID
     * @param paymentKey 결제 키
     * @param amount 결제 금액
     * @return 결제 응답 데이터
     * @throws ApiException DESIGNER_NOT_EXIST: 디자이너가 존재하지 않을 경우 PAYMENT_PROCESS_FAILED: 결제 실패 시
     */
    public PaymentResponseDto processDesignerPayment(
            Long designerId, String paymentKey, BigDecimal amount) {
        // 1. 디자이너 존재 여부 확인
        Designer designer =
                designerRepository
                        .findById(designerId)
                        .orElseThrow(() -> new ApiException(ErrorCode.DESIGNER_NOT_EXIST));

        String billingCode = generateOrderId();
        designer.updateBillingCode(billingCode);
        designerRepository.save(designer);

        // 2. 결제 서버 URL 설정
        String paymentUrl =
                paymentServerConfig.getLocalPaymentServerUrl() + "/v1/designer/toss/confirm";

        try {
            // 3. 결제 요청 데이터 생성 및 서버 호출
            PaymentResponseDto paymentResponse =
                    restTemplate.postForObject(
                            paymentUrl,
                            createPaymentRequest(paymentKey, billingCode, amount),
                            PaymentResponseDto.class);

            // 4. 결제 응답 검증
            if (paymentResponse == null || !"DONE".equalsIgnoreCase(paymentResponse.getStatus())) {
                throw new ApiException(ErrorCode.PAYMENT_PROCESS_FAILED);
            }

            return paymentResponse;

        } catch (ApiException ae) {
            // 5. 결제 실패 예외 처리
            throw ae;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.PAYMENT_PROCESS_FAILED);
        }
    }

    private PaymentRequestDto createPaymentRequest(
            String paymentKey, String billingCode, BigDecimal amount) {
        return PaymentRequestDto.builder()
                .paymentKey(paymentKey)
                .orderId(billingCode)
                .amount(amount)
                .build();
    }

    private String generateOrderId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
