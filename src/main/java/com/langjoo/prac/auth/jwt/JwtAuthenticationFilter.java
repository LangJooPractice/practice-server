package com.langjoo.prac.auth.jwt; // 패키지명은 프로젝트에 맞게 수정하세요.

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    // 💡 UserDetailsService: Spring Security에서 사용자 정보를 로드하는 인터페이스 (구현체가 필요합니다)
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 JWT 토큰 추출 ("Bearer xxxxxx" 형태)
        String jwt = resolveToken(request);

        // 2. 토큰 유효성 검사 및 인증 처리
        if (jwt != null && tokenProvider.validateToken(jwt)) {
            // 토큰이 유효한 경우
            Long userId = tokenProvider.getUserIdFromToken(jwt);

            // UserDetailsService를 통해 사용자 정보를 로드 (DB 조회)
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // Security Context에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // "Bearer " 접두사를 제거하고 실제 JWT 토큰만 추출하는 헬퍼 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}