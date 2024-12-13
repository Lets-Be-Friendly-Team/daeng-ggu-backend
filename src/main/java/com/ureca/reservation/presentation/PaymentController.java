package com.ureca.reservation.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.reservation.application.DesignerPaymentService;
import com.ureca.reservation.presentation.dto.PaymentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("daengggu")
public class PaymentController {

    private final DesignerPaymentService designerPaymentService;

    /**
     * 디자이너 결제 진행
     *
     * @param designerId 디자이너의 고유 ID
     * @param paymentKey
     * @param amount
     * @return 결제 처리 결과
     */
    @PostMapping("/designer/{designerId}/payment")
    @Operation(summary = "디자이너 결제", description = "[DLOG2200] 디자이너 결제를 진행합니다.")
    public ResponseDto<PaymentResponseDto> processDesignerPayment(
            @PathVariable Long designerId,
            @RequestParam String paymentKey,
            @RequestParam BigDecimal amount) {
        return ResponseUtil.SUCCESS(
                "디자이너 결제 성공",
                designerPaymentService.processDesignerPayment(designerId, paymentKey, amount));
    }
}
