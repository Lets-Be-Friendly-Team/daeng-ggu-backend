package com.ureca.profile.presentation;

import com.ureca.common.application.AuthService;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.profile.application.CustomerService;
import com.ureca.profile.presentation.dto.BookmarkYn;
import com.ureca.profile.presentation.dto.CustomerDetail;
import com.ureca.profile.presentation.dto.CustomerProfile;
import com.ureca.profile.presentation.dto.CustomerUpdate;
import com.ureca.profile.presentation.dto.CustomerViewDesignerProfile;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/daengggu")
@RestController
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired private CustomerService customerService;
    @Autowired private AuthService authService;

    @GetMapping("/customer/profile")
    @Operation(summary = "보호자 프로필", description = "[MYP1000] 보호자 프로필 조회 API")
    public ResponseDto<CustomerProfile> customerProfile(
            @RequestParam(defaultValue = "") Long customerId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        // service - 보호자 프로필
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.getCustomerProfile(customerId));
    }

    @GetMapping("/customer/profile/detail")
    @Operation(summary = "보호자 프로필 상세 조회", description = "[MYP2000] 보호자 프로필 상세 조회 API")
    public ResponseDto<CustomerDetail> customerDetail(
            @RequestParam(defaultValue = "") Long customerId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        // service - 보호자 프로필 상세 조회
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.getCustomerDetail(customerId));
    }

    @PatchMapping("/customer/profile/update")
    @Operation(summary = "보호자 프로필 수정", description = "[MYP2000] 보호자 프로필 수정 API")
    public ResponseDto<Void> customerUpdate(
            @RequestBody CustomerUpdate data,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        // service - 보호자 프로필 수정
        customerService.updateCustomerProfile(data, id);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @GetMapping("/customer/bookmark")
    @Operation(summary = "보호자 디자이너 찜하기", description = "[DMYP1000] 보호자 디자이너 찜하기 API")
    public ResponseDto<BookmarkYn> customerBookmark(
            @RequestParam(defaultValue = "") Long customerId,
            @RequestParam(defaultValue = "") Long designerId,
            @RequestParam(defaultValue = "") Boolean bookmarkYn,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        // service - 북마크 테이블 업데이트
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.", customerService.updateBookmark(id, designerId, bookmarkYn));
    }

    @GetMapping("/customer/designer/profile")
    @Operation(summary = "보호자가 보는 디자이너 프로필", description = "[DMYP1000] 보호자가 보는 디자이너 프로필 API")
    public ResponseDto<CustomerViewDesignerProfile> designerProfile(
            @RequestParam(defaultValue = "") Long customerId,
            @RequestParam(defaultValue = "") Long designerId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        // service - 디자이너 프로필
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.", customerService.getCustomerViewDesignerProfile(id, designerId));
    }
}
