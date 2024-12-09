package com.ureca.login.presentation;

import com.ureca.common.util.CookieUtil;
import com.ureca.login.application.KakaoService;
import com.ureca.login.application.LoginService;
import com.ureca.login.presentation.dto.KakaoDTO;
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
     *     (getKakaoInfo) 토큰을 사용해서 사용자 정보 가져오기 요청 (getUserInfoWithToken) 사용자 정보를 JWT에 담아서 리다이렉트
     *     (generateJwtToken)
     * @return JWT 담은 쿠키를 헤더에 담아 리다이렉트
     */
    @GetMapping("/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // service - 유저 정보 반환
        KakaoDTO kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"), CUSTOMER);
        // service - 토큰 발급
        String jwt = loginService.generateJwtToken(kakaoInfo);
        // util - 쿠키 생성
        String cookie = CookieUtil.createCookies(jwt);

        response.setHeader("Set-Cookie", cookie);
        response.sendRedirect(LOGIN_REDIRECT_URL);
    }

    /**
     * @title 디자이너 - 액세스 토큰 및 유저 정보 요청
     * @description 로그인 성공 시, 카카오 플랫폼 서버에서 인가 코드 가지고 리다이렉션 (callback) 인가 코드를 사용해서 토큰 발급을 요청
     *     (getKakaoInfo) 토큰을 사용해서 사용자 정보 가져오기 요청 (getUserInfoWithToken) 사용자 정보를 JWT에 담아서 리다이렉트
     *     (generateJwtToken)
     * @return JWT 담은 쿠키를 헤더에 담아 리다이렉트
     */
    @GetMapping("/callback/designer")
    public void callbackDesigner(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // service - 유저 정보 반환
        KakaoDTO kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"), DESIGNER);
        // service - 토큰 발급
        String jwt = loginService.generateJwtToken(kakaoInfo);
        // util - 쿠키 생성
        String cookie = CookieUtil.createCookies(jwt);

        response.setHeader("Set-Cookie", cookie);
        response.sendRedirect(LOGIN_REDIRECT_URL);
    }
}
