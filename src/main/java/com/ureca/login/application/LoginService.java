package com.ureca.login.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.util.CookieUtil;
import com.ureca.common.util.TokenUtils;
import com.ureca.login.presentation.dto.KakaoDTO;
import com.ureca.login.presentation.dto.UserDTO;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class LoginService {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer:";
    public static final String KEY_ROLES = "roles";
    public static final long TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 5; // 5시간

    private static final String CUSTOMER = "C";
    private static final String DESIGNER = "D";

    @Autowired private CustomerRepository customerRepository;
    @Autowired private DesignerRepository designerRepository;

    /**
     * @title JWT 발급
     * @return String JWT
     */
    public String generateJwtToken(KakaoDTO kakaoInfo) {
        String resultToken = TokenUtils.generateJwtToken(kakaoInfo);
        return resultToken;
    }

    /**
     * @title 로그인 사용자 정보 조회
     * @description 토큰에서 정보 꺼내고, DB에 존재하는지 확인
     * @return String 사용자정보
     */
    public UserDTO getLoginUserInfo(KakaoDTO kakaoDTO) {

        String email = kakaoDTO.getEmail();
        String role = kakaoDTO.getRole();
        String loginId = email + "_" + kakaoDTO.getId();

        String joinYn = "";
        long id = 0L;

        switch (role) {
            case CUSTOMER:
                Optional<Customer> customer =
                        customerRepository.findByEmailAndCustomerLoginId(email, loginId);
                if (customer.isEmpty()) {
                    joinYn = "N";
                } else {
                    joinYn = "Y";
                    id = customer.get().getCustomerId();
                }
                break;
            case DESIGNER:
                Optional<Designer> designer =
                        designerRepository.findByEmailAndDesignerLoginId(email, loginId);
                if (designer.isEmpty()) {
                    joinYn = "N";
                } else {
                    joinYn = "Y";
                    id = designer.get().getDesignerId();
                }
                break;
        }
        //  검증 결과 매핑해서 반환하기
        UserDTO userDTO =
                UserDTO.builder()
                        .joinYn(joinYn)
                        .userType(role)
                        .id(id)
                        .email(email)
                        .loginId(loginId)
                        .refreshToken(kakaoDTO.getRefreshToken())
                        .build();

        return userDTO;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. JWT 쿠키 가져오기
        Cookie cookie = CookieUtil.getJwtFromCookies(request);
        if (cookie == null) {
            throw new ApiException(ErrorCode.JWT_NOT_EXIST);
        }

        // 2. JWT 유효성 확인
        boolean isValid = TokenUtils.isValidToken(cookie.getValue());
        if (!isValid) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        // 3. 쿠키 무효화 처리 (Max-Age 0으로 설정)
        Cookie invalidatedCookie = new Cookie(cookie.getName(), null);
        invalidatedCookie.setHttpOnly(true);
        invalidatedCookie.setSecure(true);
        invalidatedCookie.setMaxAge(0); // 즉시 만료
        invalidatedCookie.setPath("/"); // 모든 경로에서 삭제
        response.addCookie(invalidatedCookie);
    }
}
