package com.langjoo.prac.auth.config;

import com.langjoo.prac.auth.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.http.HttpMethod; // HttpMethod import 필요
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 📌 설정 파일임을 명시
@EnableWebSecurity // Web Security 활성화
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; // 📌 필터 주입

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // 1. 🔑 PasswordEncoder Bean 등록 (기존 코드를 그대로 유지)
    // 이 Bean은 UserService와 AuthService에서 비밀번호 해싱/검증에 사용됩니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 🛡️ SecurityFilterChain Bean 등록 (새로 추가)
    // 이 Bean이 HTTP 요청에 대한 보안 규칙을 정의합니다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 보호 기능 비활성화 (JWT 사용 시 일반적으로 필요 없음)
                .csrf(AbstractHttpConfigurer::disable)

                // 📌 2. H2 콘솔을 위한 헤더 설정 추가 (누락된 부분)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // 👈 이 코드가 추가되어야 합니다.

                // 2. 세션 사용 비활성화 (JWT 기반 인증은 서버에 상태를 저장하지 않는 Stateless 방식)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. HTTP 요청에 대한 접근 규칙 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 📌 H2 콘솔 경로는 인증 없이 접근 허용 (가장 중요)
                        .requestMatchers("/h2-console/**").permitAll() // 👈 이 경로를 추가

                        // 📌 회원가입 및 로그인 경로는 인증 없이 접근 허용 (401 오류 해결)
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()

                        // (선택적) 프로필 조회도 인증 없이 접근 허용
                        .requestMatchers(HttpMethod.GET, "/api/users/*").permitAll()

                        // 그 외 모든 요청은 인증 필요 (토큰이 있어야 접근 가능)
                        .anyRequest().authenticated()
                );

        // 4. (추후 JWT 검증 필터를 여기에 추가해야 합니다.)
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}