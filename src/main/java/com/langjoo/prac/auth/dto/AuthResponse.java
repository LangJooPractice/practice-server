package com.langjoo.prac.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    // JWT Access Token (실제 API 요청 시 사용)
    private final String accessToken;

    // JWT Refresh Token (Access Token 만료 시 재발급에 사용, 보안 강화 목적)
    private final String refreshToken;

    // 토큰 타입 (예: "Bearer ")
    private final String tokenType = "Bearer";

    // (선택적) 사용자 ID 또는 Username을 함께 반환하여 클라이언트 편의성 증대
    private final String username;
}