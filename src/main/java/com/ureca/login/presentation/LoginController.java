package com.ureca.login.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.login.application.KakaoService;
import com.ureca.login.application.LoginService;
import com.ureca.login.presentation.dto.KakaoDTO;
import com.ureca.login.presentation.dto.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseDto<UserDTO> loginCallback(HttpServletRequest request) {
        KakaoDTO kakaoDTO = null;
        for (Cookie cookie : request.getCookies()) {
            String jwt = cookie.getAttribute("token");
            // TODO jwt에서 사용자 정보 꺼내기
        }
        // service - 카카오 로그인 사용자 정보 조회
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", loginService.getLoginUserInfo(kakaoDTO));
    }
}
