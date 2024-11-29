package com.ureca.profile.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.profile.application.service.DesignerService;
import com.ureca.profile.presentation.dto.DesignerDetail;
import com.ureca.profile.presentation.dto.DesignerProfile;
import com.ureca.profile.presentation.dto.PortfolioDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/daengggu")
@RestController
public class DesignerController {

    private static final Logger logger = LoggerFactory.getLogger(DesignerController.class);

    @Autowired private DesignerService designerService;

    /**
     * @title 디자이너 - 프로필
     * @param designerId 디자이너 아이디
     * @description /daengggu/designer/profile
     */
    @GetMapping("/designer/profile")
    public ResponseDto<DesignerProfile> designerProfile(
            @RequestParam(defaultValue = "") Long designerId) {
        // service - 디자이너 프로필
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", designerService.getDesignerProfile(designerId));
    } // designerProfile

    /**
     * @title 디자이너 - 프로필 상세
     * @param designerId 디자이너 아이디
     * @description /daengggu/designer/profile/detail
     */
    @GetMapping("/designer/profile/detail")
    public ResponseDto<DesignerDetail> designerDetail(
            @RequestParam(defaultValue = "") Long designerId) {
        // service - 디자이너 프로필 상세
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", designerService.getDesignerDetail(designerId));
    } // designerDetail

    /**
     * @title 디자이너 - 포트폴리오 상세
     * @param designerId 디자이너 아이디
     * @param portfolioId 포트폴리오 아이디
     * @description /designer/portfolio/detail
     */
    @GetMapping("/designer/portfolio/detail")
    public ResponseDto<PortfolioDetail> designerPortfolioDetail(
            @RequestParam(defaultValue = "") Long designerId,
            @RequestParam(defaultValue = "") Long portfolioId) {
        // service - 디자이너 포트폴리오 상세
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.",
                designerService.getDesignerPortfolioDetail(designerId, portfolioId));
    } // designerPortfolioDetail
}