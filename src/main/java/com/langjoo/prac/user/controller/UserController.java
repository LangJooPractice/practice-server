package com.langjoo.prac.user.controller;

import com.langjoo.prac.auth.config.UserDetailsImpl; // 인증된 사용자 정보를 가정
import com.langjoo.prac.auth.dto.AuthResponse; // 로그인 응답 (JWT 토큰 포함)
import com.langjoo.prac.auth.dto.LoginRequest; // 로그인 요청 DTO
import com.langjoo.prac.user.dto.UserRegisterRequest; // 회원가입 요청 DTO
import com.langjoo.prac.user.dto.UserProfileResponse; // 프로필 조회 응답 DTO
import com.langjoo.prac.user.dto.UserUpdateRequest;
import com.langjoo.prac.user.service.UserService;
import com.langjoo.prac.auth.service.AuthService; // 인증 서비스 분리 가정

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users") // 💡 사용자 관련 API 루트 경로
public class UserController {

    private final UserService userService;
    private final AuthService authService; // 인증 로직을 전담하는 서비스 주입

    // 1. 신규 회원 가입
    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        // Service 계층에서 ID 중복 확인, 비밀번호 해싱 후 User 엔티티 저장
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
    }

    // 2. 로그인
    // POST /api/users/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        // AuthService에서 인증 처리 후 JWT 토큰을 포함한 응답 반환
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


    // 3. 프로필 보기
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            // 📌 [수정] 현재 로그인 사용자 정보(ID)를 가져옵니다.
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable String username) {

        // 📌 [수정] 현재 사용자의 ID를 서비스로 전달합니다.
        Long currentUserId = currentUser.getUserId();

        // Service 계층으로 ID와 username 모두 전달
        UserProfileResponse response = userService.getUserProfile(currentUserId, username);
        return ResponseEntity.ok(response);
    }


    // 프로필 수정
    @PatchMapping // 👈 PATCH 메서드를 사용하여 부분 업데이트를 나타냅니다.
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            // 📌 현재 로그인 사용자 ID
            @AuthenticationPrincipal UserDetailsImpl currentUser,

            // 📌 요청 본문에서 DTO를 받습니다. @Valid로 유효성 검사 수행.
            @RequestBody @Valid UserUpdateRequest request) {

        // Service 계층으로 ID와 요청 DTO 전달
        UserProfileResponse response = userService.updateUser(currentUser.getUserId(), request);

        // 업데이트된 리소스와 함께 200 OK 반환
        return ResponseEntity.ok(response);
    }



    // 5. 회원 탈퇴
    // DELETE /api/users
    @DeleteMapping
    public ResponseEntity<Void> deactivateUser(
            @AuthenticationPrincipal UserDetailsImpl currentUser) { // 현재 로그인 사용자 확인

        // Service 계층에서 현재 사용자 ID를 받아 계정 비활성화 또는 삭제 처리
        userService.deactivateUser(currentUser.getUserId());
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}