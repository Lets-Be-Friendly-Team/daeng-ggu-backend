package com.ureca.common.util;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

// 쿠키 관련된 기능 Util
public class CookieUtil {

    /**
     * @title 쿠키 생성
     * @param jwt 토큰
     * @description 발급한 토큰을 쿠키에 담는다.
     * @return String 생성한 쿠키를 텍스트로 전달
     */
    public static String createCookies(String jwt) {
        // 쿠키 생성
        ResponseCookie cookie =
                ResponseCookie.from("jwt", jwt)
                        .maxAge(7 * 24 * 60 * 60)
                        .path("/")
                        .secure(true)
                        .sameSite("None")
                        .httpOnly(true)
                        .build();

        return cookie.toString();
    }

    /**
     * @title 요청 jwt 값 추출
     * @param request 요청
     * @description 요청 헤더에서 jwt 쿠키를 찾아서 값을 반환한다.
     * @return String jwt
     */
    public static String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) { // 모든 쿠키 반복, "jwt" 쿠키 찾기
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue(); // "jwt" 쿠키의 값을 반환
                }
            }
        } else {
            throw new ApiException(ErrorCode.COOKIE_NOT_EXIST);
        }
        return null; // "jwt" 쿠키가 없으면 null을 반환
    }
}
