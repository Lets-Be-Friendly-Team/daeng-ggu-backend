package com.ureca.profile.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.profile.application.CustomerService;
import com.ureca.profile.presentation.dto.BookmarkYn;
import com.ureca.profile.presentation.dto.CustomerDetail;
import com.ureca.profile.presentation.dto.CustomerProfile;
import com.ureca.profile.presentation.dto.CustomerSignup;
import com.ureca.profile.presentation.dto.CustomerUpdate;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/daengggu")
@RestController
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired private CustomerService customerService;

    @GetMapping("/customer/profile")
    @Operation(summary = "보호자 프로필", description = "[MYP1000] 보호자 프로필 조회 API")
    public ResponseDto<CustomerProfile> customerProfile(
            @RequestParam(defaultValue = "") Long customerId) {
        // service - 보호자 프로필
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.getCustomerProfile(customerId));
    }

    @GetMapping("/customer/profile/detail")
    @Operation(summary = "보호자 프로필 상세 조회", description = "[MYP2000] 보호자 프로필 상세 조회 API")
    public ResponseDto<CustomerDetail> customerDetail(
            @RequestParam(defaultValue = "") Long customerId) {
        // service - 보호자 프로필 상세 조회
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.getCustomerDetail(customerId));
    }

    @PostMapping(
            value = "/customer/profile/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "보호자 프로필 수정", description = "[MYP2000] 보호자 프로필 수정 API")
    public ResponseDto<Void> customerUpdate(
            @RequestBody @Valid CustomerUpdate data,
            @RequestPart(value = "newCustomerImgFile", required = false)
                    MultipartFile newCustomerImgFile)
            throws IOException {
        // service - 보호자 프로필 수정
        logger.info("data>>>" + data);
        logger.info("newCustomerImgFile>>>" + newCustomerImgFile);
        customerService.updateCustomerProfile(data, newCustomerImgFile);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @GetMapping("/customer/bookmark")
    @Operation(summary = "보호자 디자이너 찜하기", description = "[DMYP1000] 보호자 디자이너 찜하기 API")
    public ResponseDto<BookmarkYn> customerBookmark(
            @RequestParam(defaultValue = "") Long customerId,
            @RequestParam(defaultValue = "") Long designerId,
            @RequestParam(defaultValue = "") Boolean bookmarkYn) {
        // service - 북마크 테이블 업데이트
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.", customerService.updateBookmark(customerId, designerId, bookmarkYn));
    }

    @PostMapping("/customer/signup")
    @Operation(summary = "보호자 회원가입 정보 입력", description = "[LOG2000] 보호자 회원가입 정보 입력 API")
    public ResponseDto<Map<String, Long>> customerSignup(
            HttpServletRequest request, @RequestBody @Valid CustomerSignup data) {

        /* TODO 공통 필터로 빼기
        Cookie cookie = CookieUtil.getJwtFromCookies(request); // util - 쿠키에서 jwt 꺼내기
        if (cookie == null) throw new ApiException(ErrorCode.JWT_NOT_EXIST);
        boolean isValid = TokenUtils.isValidToken(cookie.getValue()); // util - 유효한 토큰인지 확인
        KakaoDTO kakaoDTO = null;
        if (!isValid) throw new ApiException(ErrorCode.INVALID_TOKEN);
        kakaoDTO = TokenUtils.parseTokenToUserInfo(cookie.getValue()); // util - jwt 기반 사용자 정보 꺼내기
        String email = kakaoDTO.getEmail();
        String role = kakaoDTO.getRole();
        */
        // TODO TEST
        String email = "";
        String role = "C";
        // service - 보호자 회원가입
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.", customerService.insertCustomer(data, email, role));
    }
}
