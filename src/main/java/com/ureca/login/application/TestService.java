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
import jakarta.servlet.http.Cookie;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private DesignerRepository designerRepository;

    private static final String CUSTOMER = "C";
    private static final String DESIGNER = "D";

    /**
     * @title Test 사용자 정보 존재 체크
     * @description 입력받은 정보 기준 사용자 정보 있는지 확인
     * @return Boolean 존재유무
     */
    public String checkTestLoginUserInfo(String userType, String email) {
        String joinYn = "";

        switch (userType) {
            case CUSTOMER:
                Optional<Customer> customer = customerRepository.findByEmail(email);
                if (customer.isEmpty()) joinYn = "N";
                else {
                    joinYn = "Y";
                }
                break;
            case DESIGNER:
                Optional<Designer> designer = designerRepository.findByEmail(email);
                if (designer.isEmpty()) joinYn = "N";
                else {
                    joinYn = "Y";
                }
                break;
        }

        return joinYn;
    } // checkTestLoginUserInfo

    /**
     * @title Test 사용자 정보 생성
     * @description 입력받은 정보 기준 사용자 정보 생성 - 기존 Login Step 2 대체
     * @return KakaoDTO 테스트 사용자 정보
     */
    public KakaoDTO generateTestUserInfo(String userType, String email, String joinYn) {

        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        long count = 0L, idNum = 0L, id = 0L;
        int cnt = 0, num = 0;
        String str = "";

        if ("N".equals(joinYn)) { // 신규 가입

            // 고유번호 생성 (기존 Login Step 2 대체)
            switch (userType) {
                case CUSTOMER:
                    count = customerRepository.count(); // long 타입
                    idNum = customerRepository.findMaxCustomerId(); // long 타입
                    break;
                case DESIGNER:
                    count = designerRepository.count();
                    idNum = designerRepository.findMaxDesignerId();
                    break;
            }
            cnt = (int) count + 1;
            num = (int) idNum + 1;
            str = "" + cnt + num;
            id = Long.parseLong(str);
        } else {

            // 고유번호 꺼내기 (기존 Login Step 2 대체)
            switch (userType) {
                case CUSTOMER:
                    Optional<Customer> customer = customerRepository.findByEmail(email);
                    str = customer.get().getCustomerLoginId();
                    break;
                case DESIGNER:
                    Optional<Designer> designer = designerRepository.findByEmail(email);
                    str = designer.get().getDesignerLoginId();
                    break;
            }
            int lastIndex = str.lastIndexOf("_");
            if (lastIndex != -1) {
                String result = str.substring(lastIndex + 1);
                id = Long.parseLong(result); // 존재하는 고유번호 출력
            } else {
                throw new ApiException(ErrorCode.USER_DATA_NOT_EXIST);
            }
        }
        KakaoDTO testDTO =
                KakaoDTO.builder()
                        .id(id)
                        .email(email)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .role(userType)
                        .build();

        return testDTO;
    } // generateTestUserInfo

    /**
     * @title Test JWT 발급
     * @return String JWT
     */
    public String generateTestJwtToken(KakaoDTO testDTO) {
        // 토큰 발급
        String resultTestToken = TokenUtils.generateJwtToken(testDTO);
        return resultTestToken;
    } // generateTestJwtToken

    /**
     * @title Test Cookie 생성
     * @return String cookieHeader
     */
    public String createTestCookie(String jwt) {
        // 테스트 쿠키 생성
        Cookie cookie = CookieUtil.createCookies(jwt);
        String cookieHeader =
                String.format(
                        "jwt=%s; HttpOnly; Secure; Max-Age=%d; Path=%s; SameSite=%s",
                        cookie.getValue(), cookie.getMaxAge(), cookie.getPath(), "None");
        return cookieHeader;
    } // createTestCookie

    /**
     * @title Test 로그인 사용자 정보 조회s
     * @description 전달 받은 사용자 정보 DB에 존재하는지 확인
     * @return String 사용자정보
     */
    public UserDTO getTestLoginUserInfo(KakaoDTO testDTO) {

        String email = testDTO.getEmail();
        String role = testDTO.getRole();
        String loginId = email + "_" + testDTO.getId();

        String joinYn = "";
        long id = 0L;

        switch (role) {
            case CUSTOMER:
                Optional<Customer> customer = customerRepository.findByEmail(email);
                if (customer.isEmpty()) joinYn = "N";
                else {
                    joinYn = "Y";
                    id = customer.get().getCustomerId();
                }
                break;
            case DESIGNER:
                Optional<Designer> designer = designerRepository.findByEmail(email);
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
                        .loginId(loginId)
                        .refreshToken(testDTO.getRefreshToken())
                        .build();

        return userDTO;
    } // getTestLoginUserInfo
}
