package com.langjoo.prac.auth.service;

import com.langjoo.prac.auth.dto.AuthResponse;
import com.langjoo.prac.auth.dto.LoginRequest;
import com.langjoo.prac.auth.jwt.JwtTokenProvider; // 👈 JWT 토큰 생성 유틸리티 주입
import com.langjoo.prac.domain.User;
import com.langjoo.prac.user.repository.UserRepository;
import com.langjoo.prac.common.exception.AuthException; // 인증 실패 예외
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 로그인 로직은 데이터 변경이 없으므로 ReadOnly
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider; // 👈 JWT 토큰 제공자

    // -------------------------------------------------------------
    // 1. 사용자 로그인 처리 및 JWT 토큰 발급
    // -------------------------------------------------------------
    @Override
    public AuthResponse login(LoginRequest request) {

        // 1. loginId를 사용하여 User 조회
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new AuthException("로그인 ID 또는 비밀번호가 일치하지 않습니다.")); // 명확한 오류 메시지 회피

        // 2. 비밀번호 일치 여부 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("로그인 ID 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. 📌 인증 성공: Access Token과 Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getLoginId());

        // 4. (선택적) Refresh Token을 DB나 Redis에 저장하여 관리 (여기서는 생략)

        // 5. AuthResponse DTO 구성 및 반환
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername()) // 사용자 친화적인 @아이디 반환
                .build();
    }
}