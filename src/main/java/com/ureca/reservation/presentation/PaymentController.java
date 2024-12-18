package com.ureca.reservation.presentation;

import com.ureca.common.application.AuthService;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.reservation.application.DesignerPaymentService;
import com.ureca.reservation.presentation.dto.PaymentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final AuthService authService;

    /**
     * 디자이너 결제 진행
     *
     * @param paymentKey 결제 키
     * @param amount 결제 금액
     * @return 결제 처리 결과
     */
    @PostMapping("/designer/payment")
    @Operation(summary = "디자이너 결제", description = "[DLOG2200] 디자이너 결제를 진행합니다.")
    public ResponseDto<PaymentResponseDto> processDesignerPayment(
            @RequestParam String paymentKey,
            @RequestParam BigDecimal amount,
            HttpServletRequest request,
            HttpServletResponse response) {
        // TODO: 소셜 로그인 ID 처리 필요
        Long id = authService.getRequestToUserId(request);
        // Long designerId = 4L; // 임시 더미값
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS(
                "디자이너 결제 성공",
                designerPaymentService.processDesignerPayment(id, paymentKey, amount));
    }
}
