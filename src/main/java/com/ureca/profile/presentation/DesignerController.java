package com.ureca.profile.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.profile.application.DesignerService;
import com.ureca.profile.presentation.dto.DesignerDetail;
import com.ureca.profile.presentation.dto.DesignerProfile;
import com.ureca.profile.presentation.dto.DesignerRegister;
import com.ureca.profile.presentation.dto.DesignerUpdate;
import com.ureca.profile.presentation.dto.PortfolioDetail;
import com.ureca.profile.presentation.dto.PortfolioUpdate;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/daengggu")
@RestController
public class DesignerController {

    private static final Logger logger = LoggerFactory.getLogger(DesignerController.class);

    @Autowired private DesignerService designerService;

    @GetMapping("/designer/profile")
    @Operation(summary = "디자이너 프로필", description = "[DMYP1000] 디자이너 프로필 API")
    public ResponseDto<DesignerProfile> designerProfile(
            @RequestParam(defaultValue = "") Long designerId) {
        // service - 디자이너 프로필
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", designerService.getDesignerProfile(designerId));
    }

    @GetMapping("/designer/profile/detail")
    @Operation(summary = "디자이너 프로필 상세", description = "[DMYP2000] 디자이너 프로필 상세 API")
    public ResponseDto<DesignerDetail> designerDetail(
            @RequestParam(defaultValue = "") Long designerId) {
        // service - 디자이너 프로필 상세
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", designerService.getDesignerDetail(designerId));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/designer/profile/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "디자이너 프로필 수정", description = "[DMYP2000] 디자이너 프로필 수정 API")
    public ResponseDto<Void> designerUpdate(@ModelAttribute DesignerUpdate data) {
        // service - 보호자 프로필 수정
        designerService.updateDesignerProfile(data);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @GetMapping("/designer/portfolio/detail")
    @Operation(summary = "디자이너 포트폴리오 조회", description = "[DMYP2100] 디자이너 포트폴리오 상세 조회 API")
    public ResponseDto<PortfolioDetail> designerPortfolioDetail(
            @RequestParam(defaultValue = "") Long designerId,
            @RequestParam(defaultValue = "") Long portfolioId) {
        // service - 디자이너 포트폴리오 상세
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.",
                designerService.getDesignerPortfolioDetail(designerId, portfolioId));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/designer/portfolio/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "디자이너 포트폴리오 수정", description = "[DLOG3110] 디자이너 포트폴리오 수정 API")
    public ResponseDto<Void> designerUpdate(@ModelAttribute PortfolioUpdate data) {
        // service - 디자이너 포트폴리오 수정
        designerService.updateDesignerPortfolio(data);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @DeleteMapping("/designer/portfolio/delete/{designerId}/{portfolioId}")
    @Operation(summary = "디자이너 포트폴리오 삭제", description = "[DMYP2100] 디자이너 포트폴리오 삭제 API")
    public ResponseDto<Void> designerPortfolioDelete(
            @PathVariable Long designerId, @PathVariable Long portfolioId) {
        // service - 디자이너 포트폴리오 삭제
        designerService.deleteDesignerPortfolio(designerId, portfolioId);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @PostMapping("/designer/register/profile")
    @Operation(summary = "디자이너 프로필 등록", description = "[DLOG3100] 회원가입 시 디자이너 프로필 등록 API")
    public ResponseDto<Void> designerRegisterProfile(@RequestBody DesignerRegister data) {
        // service - 보호자 프로필 등록
        designerService.registerDesignerProfile(data);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }
}
