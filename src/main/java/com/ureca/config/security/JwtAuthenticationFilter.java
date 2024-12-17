package com.ureca.config.security;

import com.ureca.common.util.TokenUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired private TokenUtils tokenUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = TokenUtils.getTokenFromHeader(header);
                Claims claims =
                        Jwts.parser()
                                .setSigningKey(TokenUtils.jwtSecretKey)
                                .parseClaimsJws(token)
                                .getBody();

                String userId = claims.get("userId", String.class);
                String role = claims.get("role", String.class);

                // 권한 정보 추가
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                // SecurityContext에 Authentication 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // 토큰 검증 실패 시 로그만 남기고 다음 필터로 넘김
                logger.error("Token verification failed: ", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
