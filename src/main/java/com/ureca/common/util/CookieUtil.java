package com.ureca.common.util;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

// 쿠키 관련된 기능 Util
public class CookieUtil {

    /**
     * @title 쿠키 생성
     * @param jwt 토큰
     * @description 발급한 토큰을 쿠키에 담는다.
     * @return String 생성한 쿠키를 텍스트로 전달
     */
    public static Cookie createCookies(String jwt) {
        // 쿠키 생성
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true); // 보안을 위해 HttpOnly 설정
        cookie.setSecure(true); // HTTPS 환경에서만 쿠키를 전송하도록 설정 (필요에 따라)
        cookie.setMaxAge(60 * 60); // 쿠키 유효 시간 설정 (1시간)
        cookie.setPath("/"); // 모든 경로에서 접근할 수 있도록 설정
        cookie.setDomain("localhost");
        return cookie;
    }

    /**
     * @title 요청 jwt 값 추출
     * @param request 요청
     * @description 요청 헤더에서 jwt 쿠키를 찾아서 값을 반환한다.
     * @return String jwt
     */
    public static Cookie getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) { // 모든 쿠키 반복, "jwt" 쿠키 찾기
                if ("jwt".equals(cookie.getName())) {
                    return cookie;
                }
            }
        } else {
            throw new ApiException(ErrorCode.COOKIE_NOT_EXIST);
        }
        return null; // "jwt" 쿠키가 없으면 null을 반환
    }
}
