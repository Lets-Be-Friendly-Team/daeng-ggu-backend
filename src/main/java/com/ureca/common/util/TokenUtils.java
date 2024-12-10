package com.ureca.common.util;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.login.presentation.dto.KakaoDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// JWT 관련된 토큰 Util
@Log4j2
@Component
public class TokenUtils {

    public static String jwtSecretKey;

    @Value("${login.jwt.secretkey}")
    public void setJwtSecretKey(String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    /**
     * 사용자 정보를 기반으로 토큰을 생성하여 반환 해주는 메서드
     *
     * @param kakaoDTO : 카카오 로그인 사용자 정보
     * @return String : 토큰
     */
    public static String generateJwtToken(KakaoDTO kakaoDTO) {
        // 사용자 시퀀스를 기준으로 JWT 토큰을 발급하여 반환해줍니다.
        JwtBuilder builder =
                Jwts.builder()
                        .setHeader(createHeader()) // Header 구성
                        .setClaims(createClaims(kakaoDTO)) // Payload - Claims 구성
                        .setSubject(kakaoDTO.getRole()) // Payload - Subject 구성
                        .signWith(SignatureAlgorithm.HS256, createSignature()) // Signature 구성
                        .setExpiration(createExpiredDate()); // Expired Date 구성
        return builder.compact();
    }

    /**
     * 토큰을 기반으로 사용자 정보를 반환 해주는 메서드
     *
     * @param token String : 토큰
     * @return String : 사용자 정보
     */
    public static KakaoDTO parseTokenToUserInfo(String token) {
        // JWT 파싱
        Claims claims =
                Jwts.parser()
                        .setSigningKey(jwtSecretKey) // 비밀 키로 서명 검증
                        .parseClaimsJws(token) // JWT 파싱
                        .getBody(); // Payload (Claims) 추출

        // Claims에서 kakaoDTO에 해당하는 값들을 꺼내서 KakaoDTO 객체 생성
        String email = claims.get("userEmail", String.class);
        String accessToken = claims.get("accessToken", String.class);
        String refreshToken = claims.get("refreshToken", String.class);
        Long id = claims.get("userId", Long.class);
        String role = claims.getSubject(); // subject로 저장한 값 (여기서는 role)

        // KakaoDTO 객체를 생성하여 반환
        return KakaoDTO.builder()
                .id(id)
                .email(email)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(role)
                .build();
    }

    /**
     * 유효한 토큰인지 확인 해주는 메서드
     *
     * @param token String : 토큰
     * @return boolean : 유효한지 여부 반환
     */
    public static boolean isValidToken(String token) {
        try {
            Claims claims = getClaimsFormToken(token);

            // TODO 내 생각에 여기는 JWT 유효성만 확인하는 것 같다.
            //      카카오에서 발급받은 토큰 유효성 확인을 또 해줘야 할 듯 하다.
            Date expireTime = claims.getExpiration();
            Object userId = claims.get("userId");

            return true;
        } catch (ExpiredJwtException exception) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException exception) {
            throw new ApiException(ErrorCode.TOKEN_TAMPERED);
        } catch (NullPointerException exception) {
            throw new ApiException(ErrorCode.TOKEN_IS_NULL);
        }
    }

    /**
     * Header 내에 토큰을 추출합니다.
     *
     * @param header 헤더
     * @return String
     */
    public static String getTokenFromHeader(String header) {
        return header.split(" ")[1];
    }

    /**
     * 토큰의 만료기간을 지정하는 함수
     *
     * @return Calendar
     */
    private static Date createExpiredDate() {
        // 토큰 만료시간은 30일으로 설정
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 8); // 8시간
        // c.add(Calendar.DATE, 1);         // 1일
        return c.getTime();
    }

    /**
     * JWT의 "헤더" 값을 생성해주는 메서드
     *
     * @return HashMap<String, Object>
     */
    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("typ", "JWT");
        header.put("alg", "HS256");
        header.put("regDate", System.currentTimeMillis());
        return header;
    }

    /**
     * 사용자 정보를 기반으로 클래임을 생성해주는 메서드
     *
     * @param kakaoDTO 사용자 정보
     * @return Map<String, Object>
     */
    private static Map<String, Object> createClaims(KakaoDTO kakaoDTO) {
        // 공개 클레임에 사용자의 이름과 이메일을 설정하여 정보를 조회할 수 있다.
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", kakaoDTO.getId());
        claims.put("userEmail", kakaoDTO.getEmail());
        claims.put("accessToken", kakaoDTO.getAccessToken());
        claims.put("refreshToken", kakaoDTO.getRefreshToken());
        claims.put("role", kakaoDTO.getRole());
        return claims;
    }

    /**
     * UserDto JWT "서명(Signature)" 발급을 해주는 메서드 SecretKeySpec
     *
     * @return Key
     */
    private static Key createSignature() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecretKey);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * 토큰 정보를 기반으로 Claims 정보를 반환받는 메서드
     *
     * @param token : 토큰
     * @return Claims : Claims
     */
    private static Claims getClaimsFormToken(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecretKey))
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰을 기반으로 사용자 정보를 반환받는 메서드
     *
     * @param token : 토큰
     * @return String : 사용자 아이디
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return claims.get("userId").toString();
    }
}
