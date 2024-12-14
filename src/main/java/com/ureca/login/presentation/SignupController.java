package com.ureca.login.presentation;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.common.util.CookieUtil;
import com.ureca.common.util.TokenUtils;
import com.ureca.login.presentation.dto.KakaoDTO;
import com.ureca.profile.application.CustomerService;
import com.ureca.profile.application.DesignerService;
import com.ureca.profile.presentation.CustomerController;
import com.ureca.profile.presentation.dto.CustomerSignup;
import com.ureca.profile.presentation.dto.DesignerSignup;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/daengggu")
@RestController
public class SignupController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired private CustomerService customerService;
    @Autowired private DesignerService designerService;

    @PostMapping("/customer/signup")
    @Operation(summary = "보호자 회원가입 정보 입력", description = "[LOG2000] 보호자 회원가입 정보 입력 API")
    public ResponseDto<Map<String, Long>> customerSignup(
            HttpServletRequest request, @RequestBody CustomerSignup data) {

        Cookie cookie = CookieUtil.getJwtFromCookies(request); // util - 쿠키에서 jwt 꺼내기
        if (cookie == null) throw new ApiException(ErrorCode.JWT_NOT_EXIST);
        boolean isValid = TokenUtils.isValidToken(cookie.getValue()); // util - 유효한 토큰인지 확인
        KakaoDTO kakaoDTO = null;
        if (!isValid) throw new ApiException(ErrorCode.INVALID_TOKEN);
        kakaoDTO = TokenUtils.parseTokenToUserInfo(cookie.getValue()); // util - jwt 기반 사용자 정보 꺼내기

        // service - 보호자 회원가입
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.insertCustomer(data, kakaoDTO));
    }

    @PostMapping("/designer/signup")
    @Operation(summary = "디자이너 회원가입 정보 입력", description = "[DLOG3100] 디자이너 회원가입 정보 입력 API")
    public ResponseDto<Map<String, Long>> designerSignup(
            HttpServletRequest request, @RequestBody DesignerSignup data) {

        Cookie cookie = CookieUtil.getJwtFromCookies(request); // util - 쿠키에서 jwt 꺼내기
        if (cookie == null) throw new ApiException(ErrorCode.JWT_NOT_EXIST);
        boolean isValid = TokenUtils.isValidToken(cookie.getValue()); // util - 유효한 토큰인지 확인
        KakaoDTO kakaoDTO = null;
        if (!isValid) throw new ApiException(ErrorCode.INVALID_TOKEN);
        kakaoDTO = TokenUtils.parseTokenToUserInfo(cookie.getValue()); // util - jwt 기반 사용자 정보 꺼내기

        // service - 디자이너 회원가입
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", designerService.insertDesigner(data, kakaoDTO));
    }
}
