package com.ureca.login.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.login.presentation.dto.KakaoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService {

    @Value("${kakao.client.id}")
    String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect.uri}")
    String KAKAO_REDIRECT_URI;

    @Value("${kakao.client.secret}")
    String KAKAO_CLIENT_SECRET;

    @Value("${kakao.client.id.designer}")
    String KAKAO_CLIENT_ID_DESIGNER;

    @Value("${kakao.redirect.uri.designer}")
    String KAKAO_REDIRECT_URI_DESIGNER;

    @Value("${kakao.client.secret.designer}")
    String KAKAO_CLIENT_SECRET_DESIGNER;

    private static final String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private static final String KAKAO_API_URI = "https://kapi.kakao.com";

    /**
     * @title 카카오 로그인 요청 URI 매핑
     * @description 카카오 인증 URI + client_id + redirect_uri = 로그인 요청 URI 인가 코드를 요청받도록 설정
     * @return String 카카오 로그인 요청 URI
     */
    public String getKakaoLogin(String userType) {

        String kakaoUrl = "";

        switch (userType) {
            case "C":
                kakaoUrl =
                        KAKAO_AUTH_URI
                                + "/oauth/authorize"
                                + "?client_id="
                                + KAKAO_CLIENT_ID
                                + "&redirect_uri="
                                + KAKAO_REDIRECT_URI
                                + "&response_type=code";
                break;

            case "D":
                kakaoUrl =
                        KAKAO_AUTH_URI
                                + "/oauth/authorize"
                                + "?client_id="
                                + KAKAO_CLIENT_ID_DESIGNER
                                + "&redirect_uri="
                                + KAKAO_REDIRECT_URI_DESIGNER
                                + "&response_type=code";
                break;
        }

        return kakaoUrl;
    }

    /**
     * @title 토큰 발급 요청
     * @param code 인가 코드
     * @description 인가 코드를 사용해서 토큰 발급을 요청한다. 발급받은 토큰을 getUserInfoWithToken로 넘긴다.
     * @return KakaoDTO 유저 정보
     */
    public KakaoDTO getKakaoInfo(String code, String userType) throws Exception {
        if (code == null) throw new ApiException(ErrorCode.KAKAO_AUTHORIZE_DENIED);

        String accessToken = "";
        String refreshToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("code", code);

            switch (userType) {
                case "C":
                    params.add("client_id", KAKAO_CLIENT_ID);
                    params.add("client_secret", KAKAO_CLIENT_SECRET);
                    params.add("redirect_uri", KAKAO_REDIRECT_URI);
                    break;

                case "D":
                    params.add("client_id", KAKAO_CLIENT_ID_DESIGNER);
                    params.add("client_secret", KAKAO_CLIENT_SECRET_DESIGNER);
                    params.add("redirect_uri", KAKAO_REDIRECT_URI_DESIGNER);
                    break;
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity =
                    new HttpEntity<>(params, headers);

            // REST API : 토큰 발급 요청
            ResponseEntity<String> response =
                    restTemplate.exchange(
                            KAKAO_AUTH_URI + "/oauth/token",
                            HttpMethod.POST,
                            httpEntity,
                            String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            // 토큰 발급 확인
            accessToken = jsonNode.get("access_token").asText();
            refreshToken = jsonNode.get("refresh_token").asText();
        } catch (Exception e) {
            throw new Exception("API call failed");
        }

        return getUserInfoWithToken(accessToken, refreshToken, userType);
    }

    /**
     * @title 사용자 정보 가져오기 요청
     * @param accessToken 액세스 토큰
     * @description 액세스 토큰을 사용해서 사용자 정보 가져오기 요청한다. 응답받은 사용자 정보를 getKakaoInfo로 반환한다.
     * @return KakaoDTO 유저 정보
     */
    private KakaoDTO getUserInfoWithToken(String accessToken, String refreshToken, String userType)
            throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        // REST API : 사용자 정보 가져오기 요청
        ResponseEntity<String> response =
                rt.exchange(
                        KAKAO_API_URI + "/v2/user/me", HttpMethod.POST, httpEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        // 사용자 정보 확인
        Long id = jsonNode.get("id").asLong();
        JsonNode account = jsonNode.get("kakao_account");
        String email = account.get("email").asText();

        return KakaoDTO.builder()
                .id(id)
                .email(email)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(userType)
                .build();
    }
}
