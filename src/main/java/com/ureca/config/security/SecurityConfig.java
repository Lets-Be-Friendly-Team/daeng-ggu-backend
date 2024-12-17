package com.ureca.config.security;

import com.ureca.common.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
        "/daengggu/*", // 허용할 URL 경로 추가
        "/login/**", // 로그인 경로 예외처리
        "/register/**" // 회원가입 경로 예외처리
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // CORS 설정
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(
                        logout ->
                                logout.logoutUrl("/logout") // 로그아웃 엔드포인트
                                        .logoutSuccessHandler(
                                                (request, response, authentication) -> {
                                                    // 쿠키 삭제 로직 추가
                                                    Cookie jwtCookie =
                                                            CookieUtil.createCookies(
                                                                    null); // 쿠키 초기화
                                                    jwtCookie.setMaxAge(0); // 쿠키 만료
                                                    response.addCookie(jwtCookie);
                                                }))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(AUTH_WHITELIST)
                                        .permitAll() // 허용된 URL은 인증 없이 접근 가능
                                        .anyRequest()
                                        .authenticated() // 그 외 모든 요청은 인증 필요
                        )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
