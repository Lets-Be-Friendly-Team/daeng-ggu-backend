package com.ureca.login.application;

import com.ureca.login.presentation.dto.KakaoDTO;
import com.ureca.login.presentation.dto.UserDTO;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Map<String, Object> payloads = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();

        headers.put("alg", "HS256");
        headers.put("typ", "JWT");
        payloads.put("role", kakaoInfo.getEmail());
        payloads.put("email", kakaoInfo.getEmail());
        payloads.put("oauthId", kakaoInfo.getId());
        payloads.put("accessToken", kakaoInfo.getAccessToken());
        payloads.put("refreshToken", kakaoInfo.getRefreshToken());

        return ""; // TODO 토큰에 담아서 보내기
        /*
        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuer("daengguu")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 1000 * 60))
                .signWith(SignatureAlgorithm.HS256, "secret")
                .compact();*/

    }

    /**
     * @title 로그인 사용자 정보 조회
     * @description 토큰에서 정보 꺼내고, DB에 존재하는지 확인
     * @return String 사용자정보
     */
    public UserDTO getLoginUserInfo(KakaoDTO kakaoDTO) {

        String email = kakaoDTO.getEmail();
        String role = kakaoDTO.getRole();
        String loginId = email + kakaoDTO.getId();

        String joinYn = "";
        long id = 0L;

        switch (role) {
            case CUSTOMER:
                Optional<Customer> customer =
                        customerRepository.findByEmailAndCustomerLoginId(email, loginId);
                if (customer.isEmpty()) joinYn = "N";
                else {
                    joinYn = "Y";
                    id = customer.get().getCustomerId();
                }
                break;
            case DESIGNER:
                Optional<Designer> designer =
                        designerRepository.findByEmailAndDesignerLoginId(email, loginId);
                if (designer.isEmpty()) joinYn = "N";
                else {
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
                        .loginId(kakaoDTO.getId())
                        .refreshToken(kakaoDTO.getRefreshToken())
                        .build();

        return userDTO;
    }
}
