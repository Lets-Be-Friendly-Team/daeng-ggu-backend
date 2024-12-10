package com.ureca.login.presentation;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.common.util.CookieUtil;
import com.ureca.common.util.TokenUtils;
import com.ureca.login.application.KakaoService;
import com.ureca.login.application.LoginService;
import com.ureca.login.presentation.dto.KakaoDTO;
import com.ureca.login.presentation.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daengggu")
@RequiredArgsConstructor
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final KakaoService kakaoService;
    private final LoginService loginService;

    // Login Step 1
    /**
     * @title 카카오 로그인 요청
     * @param userType 사용자 구분 (C:보호자, D:디자이너)
     * @description 카카오 플랫폼 서버로 인가 코드 요청
     * @return kakaoUrl 리다이렉트 정보 포함한 uri, 로그인 성공 시 인가 코드 반환함
     */
    @GetMapping("/login")
    @Operation(summary = "카카오 로그인 요청", description = "[LOG1000] 보호자/디자이너 로그인 요청 API")
    public ResponseDto<String> loginPage(@RequestParam(defaultValue = "") String userType) {
        // service - kakaoUrl 매핑
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", kakaoService.getKakaoLogin(userType));
    }

    // Login Step 3
    /**
     * @title 카카오 로그인 응답
     * @description 카카오 로그인 사용자 정보 조회
     * @return 사용자 정보
     */
    @GetMapping("/login/callback")
    @Operation(summary = "사용자 정보 요청", description = "[LOG1000] 보호자/디자이너 사용자 정보 API")
    public ResponseDto<UserDTO> loginCallback(
            HttpServletRequest request, HttpServletResponse response) {

        // util - 쿠키에서 jwt 꺼내기
        Cookie cookie = CookieUtil.getJwtFromCookies(request);
        if (cookie == null) throw new ApiException(ErrorCode.JWT_NOT_EXIST);

        // util - 유효한 토큰인지 확인
        boolean isValid = TokenUtils.isValidToken(cookie.getValue());
        KakaoDTO kakaoDTO = null;
        if (isValid) {
            // util - jwt 기반 사용자 정보 꺼내기
            kakaoDTO = TokenUtils.parseTokenToUserInfo(cookie.getValue());
        } else {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
        // util - 쿠키 그대로 전달
        String cookieHeader =
                String.format(
                        "jwt=%s; HttpOnly; Secure; Max-Age=%d; Path=%s; SameSite=%s",
                        cookie.getValue(), cookie.getMaxAge(), cookie.getPath(), "None");
        response.setHeader("Set-Cookie", cookieHeader);
        // service - 카카오 로그인 사용자 정보 조회
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", loginService.getLoginUserInfo(kakaoDTO));
    }
}
