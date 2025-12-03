package com.langjoo.prac.auth.service;

import com.langjoo.prac.auth.dto.AuthResponse;
import com.langjoo.prac.auth.dto.LoginRequest;

public interface AuthService {

    // 1. 사용자 로그인 처리 및 JWT 토큰 발급
    AuthResponse login(LoginRequest request);

    // 2. (선택적) Refresh Token을 사용한 Access Token 재발급
    // AuthResponse reissueToken(String refreshToken);
}