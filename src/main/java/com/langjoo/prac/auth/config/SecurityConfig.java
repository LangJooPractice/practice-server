package com.langjoo.prac.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // 📌 설정 파일임을 명시
public class SecurityConfig {

    // 📌 PasswordEncoder Bean 등록
    // BCryptPasswordEncoder는 비밀번호 해싱을 위한 권장되는 구현체입니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 알고리즘을 사용하여 해시를 생성하고 검증하는 객체를 반환
        return new BCryptPasswordEncoder();
    }

    // 💡 참고: 실제 Spring Security 설정을 위한 SecurityFilterChain Bean도 이 클래스에 정의됩니다.

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     // ... JWT 필터, 인증 규칙 등 설정 ...
    // }
}