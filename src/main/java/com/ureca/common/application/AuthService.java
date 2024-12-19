package com.ureca.common.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.util.CookieUtil;
import com.ureca.common.util.TokenUtils;
import com.ureca.login.application.LoginService;
import com.ureca.login.presentation.dto.KakaoDTO;
import com.ureca.login.presentation.dto.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginService loginService;

    // 사용자 ID
    public Long getRequestToUserId(HttpServletRequest request) {

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
        UserDTO userDTO = loginService.getLoginUserInfo(kakaoDTO);

        return userDTO.getId();
    }

    // 사용자 역할
    public String getRequestToRole(HttpServletRequest request) {

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
        UserDTO userDTO = loginService.getLoginUserInfo(kakaoDTO);

        return userDTO.getUserType();
    }

    // 사용자 정보 전체
    public UserDTO getRequestToUserDTO(HttpServletRequest request) {

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
        UserDTO userDTO = loginService.getLoginUserInfo(kakaoDTO);

        return userDTO;
    }

    public String getRequestToCookieHeader(HttpServletRequest request) {

        Cookie cookie = CookieUtil.getJwtFromCookies(request);
        if (cookie == null) throw new ApiException(ErrorCode.JWT_NOT_EXIST);

        String cookieHeader =
                String.format(
                        "jwt=%s; HttpOnly; Secure; Max-Age=%d; Path=%s; SameSite=%s",
                        cookie.getValue(), cookie.getMaxAge(), cookie.getPath(), "None");

        return cookieHeader;
    }
}
