package com.ureca.login.presentation;

import com.ureca.login.application.KakaoService;
import com.ureca.login.application.LoginService;
import com.ureca.login.presentation.dto.KakaoDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Login Step 2
@Controller
@RequiredArgsConstructor
@RequestMapping("kakao")
public class KakaoController {

    private final KakaoService kakaoService;
    private final LoginService loginService;

    @Value("${login.redirect.uri}")
    String LOGIN_REDIRECT_URL;

    private static final String CUSTOMER = "C";
    private static final String DESIGNER = "D";

    /**
     * @title 보호자 - 액세스 토큰 및 유저 정보 요청
     * @description 로그인 성공 시, 카카오 플랫폼 서버에서 인가 코드 가지고 리다이렉션 (callback) 인가 코드를 사용해서 토큰 발급을 요청
     *     (getKakaoInfo) 토큰을 사용해서 사용자 정보 가져오기 요청 (getUserInfoWithToken) 사용자 정보를 반환
     * @return kakaoInfo 유저 정보
     */
    @GetMapping("/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // service - 유저 정보 반환
        KakaoDTO kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"), CUSTOMER);
        String jwt = loginService.generateJwtToken(kakaoInfo);

        // 쿠키 생성
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true); // 보안을 위해 HttpOnly 설정
        cookie.setSecure(true); // HTTPS 환경에서만 쿠키를 전송하도록 설정 (필요에 따라)
        cookie.setMaxAge(60 * 60); // 쿠키 유효 시간 설정 (1시간)
        cookie.setPath("/"); // 모든 경로에서 접근할 수 있도록 설정
        response.addCookie(cookie);

        response.sendRedirect(LOGIN_REDIRECT_URL);
    }

    /**
     * @title 디자이너 - 액세스 토큰 및 유저 정보 요청
     * @description 로그인 성공 시, 카카오 플랫폼 서버에서 인가 코드 가지고 리다이렉션 (callback) 인가 코드를 사용해서 토큰 발급을 요청
     *     (getKakaoInfo) 토큰을 사용해서 사용자 정보 가져오기 요청 (getUserInfoWithToken) 사용자 정보를 반환
     * @return kakaoInfo 유저 정보
     */
    @GetMapping("/callback/designer")
    public void callbackDesigner(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // service - 유저 정보 반환
        KakaoDTO kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"), DESIGNER);
        String jwt = loginService.generateJwtToken(kakaoInfo);

        // 쿠키 생성
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.sendRedirect(LOGIN_REDIRECT_URL);
    }
}
