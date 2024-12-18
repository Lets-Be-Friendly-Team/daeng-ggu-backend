package com.ureca.login.presentation;

import com.ureca.common.application.AuthService;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.response.ApiResponse;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.common.response.SuccessCode;
import com.ureca.common.util.CookieUtil;
import com.ureca.common.util.TokenUtils;
import com.ureca.login.application.ExternalService;
import com.ureca.login.application.TestService;
import com.ureca.login.application.dto.Coordinate;
import com.ureca.login.presentation.dto.AuthConstants;
import com.ureca.login.presentation.dto.KakaoDTO;
import com.ureca.login.presentation.dto.UserDTO;
import com.ureca.profile.application.CustomerService;
import com.ureca.profile.application.DesignerService;
import com.ureca.profile.presentation.dto.CustomerSignup;
import com.ureca.profile.presentation.dto.DesignerSignup;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/daengggu/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired private TestService testService;
    @Autowired private CustomerService customerService;
    @Autowired private DesignerService designerService;
    @Autowired private ExternalService externalService;
    @Autowired private AuthService authService;

    // Test : 기존 Login Step 1~3 합침
    /**
     * @title 테스트 로그인 요청
     * @param userType 사용자 구분 (C:보호자, D:디자이너)
     * @param email 이메일
     * @description 테스트용 일반 로그인
     * @return ResponseDto<UserDTO> 사용자 정보
     */
    @GetMapping("/login")
    @Operation(summary = "TEST 로그인 요청", description = "[LOG1000] 테스트 보호자/디자이너 로그인 요청 API")
    public ResponseDto<UserDTO> loginPage(
            HttpServletRequest request,
            @RequestParam(defaultValue = "") String userType,
            @RequestParam(defaultValue = "") String email,
            HttpServletResponse response) {

        // 존재하는 사용자인지 먼저 검증
        String joinYn = testService.checkTestLoginUserInfo(userType, email);
        // 테스트 : 토큰 발급 + 쿠키에 토큰 넣기 + 사용자 정보 반환
        KakaoDTO testDTO = testService.generateTestUserInfo(userType, email, joinYn);
        String testToken = testService.generateTestJwtToken(testDTO);
        String cookieHeader = testService.createTestCookie(testToken);
        response.setHeader("Set-Cookie", cookieHeader);
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", testService.getTestLoginUserInfo(testDTO));
    }

    @PostMapping("/customer/signup")
    @Operation(summary = "TEST 보호자 회원가입 정보 입력", description = "[LOG2000] 테스트 보호자 회원가입 정보 입력 API")
    public ResponseDto<Map<String, Long>> customerSignup(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody CustomerSignup data) {

        Cookie cookie = CookieUtil.getJwtFromCookies(request); // util - 쿠키에서 jwt 꺼내기
        if (cookie == null) throw new ApiException(ErrorCode.JWT_NOT_EXIST);
        boolean isValid = TokenUtils.isValidToken(cookie.getValue()); // util - 유효한 토큰인지 확인
        KakaoDTO kakaoDTO = null;
        if (!isValid) throw new ApiException(ErrorCode.INVALID_TOKEN);
        kakaoDTO = TokenUtils.parseTokenToUserInfo(cookie.getValue()); // util - jwt 기반 사용자 정보 꺼내기
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        // service - 보호자 회원가입
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.insertCustomer(data, kakaoDTO));
    }

    @PostMapping("/designer/signup")
    @Operation(summary = "TEST 디자이너 회원가입 정보 입력", description = "[DLOG3100] 테스트 디자이너 회원가입 정보 입력 API")
    public ResponseDto<Map<String, Long>> designerSignup(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody DesignerSignup data) {

        Cookie cookie = CookieUtil.getJwtFromCookies(request); // util - 쿠키에서 jwt 꺼내기
        if (cookie == null) throw new ApiException(ErrorCode.JWT_NOT_EXIST);
        boolean isValid = TokenUtils.isValidToken(cookie.getValue()); // util - 유효한 토큰인지 확인
        KakaoDTO kakaoDTO = null;
        if (!isValid) throw new ApiException(ErrorCode.INVALID_TOKEN);
        kakaoDTO = TokenUtils.parseTokenToUserInfo(cookie.getValue()); // util - jwt 기반 사용자 정보 꺼내기
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        // service - 디자이너 회원가입
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", designerService.insertDesigner(data, kakaoDTO));
    }

    /**
     * @title 단순 JWT 발급 확인 테스트 컨트롤러
     * @description 사용자 정보를 기반으로 JWT를 발급하는 APIApiResponse
     * @return ApiResponseWrapper<ApiResponse> : 응답 결과 및 응답 코드 반환
     */
    @PostMapping("/generateToken")
    @Operation(summary = "단순 토큰 발급 확인", description = "입력한 사용자 정보를 기반으로 테스트 JWT를 발급하는 API")
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

    /**
     * @title 단순 주소 좌표 변환 확인 테스트 컨트롤러
     * @description 주소를 입력하면 좌표값으로 반환된다.
     * @return ResponseDto<Coordinate> : x, y 좌표
     */
    @GetMapping("/kakaoAddress")
    @Operation(summary = "단순 좌표 변환 확인", description = "사용자 address2 정보를 입력하면 좌표로 반환하는 API")
    public ResponseDto<Coordinate> selectKakaoAddress(String address2) {
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", externalService.addressToCoordinate(address2));
    }
}
