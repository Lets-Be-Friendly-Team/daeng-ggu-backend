package com.ureca.login.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.login.application.ExternalService;
import io.swagger.v3.oas.annotations.Operation;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/daengggu")
@RestController
public class ExternalController {

    @Autowired private ExternalService externalService;

    @GetMapping("/designer/profile/verify/business")
    @Operation(summary = "디자이너 사업자 번호 인증", description = "[DLOG3100] 디자이너 사업자 번호 인증 API")
    public ResponseDto<String> designerBusinessVerify(
            @RequestParam(defaultValue = "") String businessNumber,
            @RequestParam(defaultValue = "") String representativeName,
            @RequestParam(defaultValue = "") String startDate)
            throws URISyntaxException {
        // service - 디자이너 사업자 번호 인증
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.",
                externalService.validateBusinessInfo(
                        businessNumber, representativeName, startDate));
    }
}
