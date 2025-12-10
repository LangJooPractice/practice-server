package com.langjoo.prac.user.service;

import com.langjoo.prac.user.dto.UserProfileResponse;
import com.langjoo.prac.user.dto.UserRegisterRequest;
import com.langjoo.prac.user.dto.UserUpdateRequest;

public interface UserService {

    // 1. 신규 회원 가입
    void registerUser(UserRegisterRequest request);


    UserProfileResponse getUserProfile(Long currentUserId, String username);

    // 3. 회원 탈퇴 (비활성화)
    void deactivateUser(Long userId);

    // 4. 유저 정보 수정
    UserProfileResponse updateUser(Long currentUserId, UserUpdateRequest request);

}
