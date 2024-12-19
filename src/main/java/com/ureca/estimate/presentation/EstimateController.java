package com.ureca.estimate.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.common.application.AuthService;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.estimate.application.EstimateService;
import com.ureca.estimate.presentation.dto.EstimateDto;
import com.ureca.estimate.presentation.dto.EstimateDtoDetail;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu")
public class EstimateController {

    private final EstimateService estimateService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @PutMapping("/bid/estimate")
    @Operation(summary = "견적서 생성", description = "[DREQ1340] 디자이너가 특정 요청서에 대한 견적서를 작성.")
    ResponseDto<String> makeEstimate(
            @RequestBody EstimateDto.Create estimateDto,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        estimateService.makeEstimate(estimateDto, id);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("견적서 생성이 완료되었습니다.", null);
    }

    @GetMapping("/bid/estimate/customer")
    @Operation(summary = "보호자 견적서 조회", description = "[REQ2000] 특정요청서에 대해 온 견적서 조회.")
    ResponseDto<List<EstimateDto.Response>> selectCustomerEstimate(
            HttpServletRequest request, HttpServletResponse response) {
        // TODO : 토큰 수정
        Long id = authService.getRequestToUserId(request);
        List<EstimateDto.Response> responseList = estimateService.selectCustomerEstimate(id);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("견적서 조회가 완료되었습니다.", responseList);
    }

    @GetMapping("/bid/estimate/designer")
    @Operation(summary = "디자이너 견적서 조회", description = "[DREQ1100] 디자이너가 작성한 이전 견적서 조회.")
    public ResponseDto<List<EstimateDto.Response>> getPreviousEstimatesByDesigner(
            HttpServletRequest request, HttpServletResponse response) {
        // TODO : 토큰 수정
        Long id = authService.getRequestToUserId(request);
        List<EstimateDto.Response> responseList =
                estimateService.getPreviousEstimatesByDesigner(id);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("디자이너 이전 견적서 조회에 성공하였습니다.", responseList);
    }

    @PostMapping("/bid/estimate")
    @Operation(summary = "견적서 세부 조회", description = "[REQ3000] 디자이너,미용사가 특정 견적서의 세부 정보 조회.")
    public ResponseDto<EstimateDtoDetail> getEstimateDetail(
            @RequestBody EstimateDto.ID estimateId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        EstimateDtoDetail responseDto =
                estimateService.getEstimateDetail(estimateId.getEstimateId());
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("견적서 세부 조회에 성공하였습니다.    ", responseDto);
    }
}
