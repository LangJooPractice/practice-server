package com.langjoo.prac.user.service;

import com.langjoo.prac.user.dto.UserProfileResponse;
import com.langjoo.prac.user.dto.UserRegisterRequest;

public interface UserService {

    // 1. 신규 회원 가입
    void registerUser(UserRegisterRequest request);

    // 2. 특정 사용자의 프로필 조회
    UserProfileResponse getUserProfile(String username);

    // 3. 회원 탈퇴 (비활성화)
    void deactivateUser(Long userId);

    // 💡 참고: 로그인 로직은 AuthService에서 처리한다고 가정합니다.

}
