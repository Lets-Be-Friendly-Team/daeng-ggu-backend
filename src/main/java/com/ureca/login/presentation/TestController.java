package com.ureca.login.presentation;

import com.ureca.common.response.ApiResponse;
import com.ureca.common.response.SuccessCode;
import com.ureca.common.util.TokenUtils;
import com.ureca.login.presentation.dto.AuthConstants;
import com.ureca.login.presentation.dto.KakaoDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/test")
public class TestController {

    /**
     * 테스트 - 사용자 정보를 기반으로 JWT를 발급하는 APIApiResponse
     *
     * @return ApiResponseWrapper<ApiResponse> : 응답 결과 및 응답 코드 반환
     */
    @PostMapping("/generateToken")
    @Operation(summary = "테스트 토큰 발급", description = "사용자 정보를 기반으로 JWT를 발급하는 API")
    public ResponseEntity<ApiResponse> selectCodeList() {

        // 테스트 데이터 세팅
        Long id = 1234L;
        String email = "test@naver.com";
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        String userType = "C";
        KakaoDTO kakaoDTO =
                KakaoDTO.builder()
                        .id(id)
                        .email(email)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .role(userType)
                        .build();

        // 토큰 발급
        String resultToken = TokenUtils.generateJwtToken(kakaoDTO);

        ApiResponse ar =
                ApiResponse.builder()
                        // BEARER {토큰} 형태로 반환을 해줍니다.
                        .result(AuthConstants.TOKEN_TYPE + " " + resultToken)
                        .resultCode(SuccessCode.SELECT.getStatus())
                        .resultMsg(SuccessCode.SELECT.getMessage())
                        .build();

        return new ResponseEntity<>(ar, HttpStatus.OK);
    }
}
