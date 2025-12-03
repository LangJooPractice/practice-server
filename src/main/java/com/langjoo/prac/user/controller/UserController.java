package com.langjoo.prac.user.controller;

import com.langjoo.prac.auth.config.UserDetailsImpl; // 인증된 사용자 정보를 가정
import com.langjoo.prac.auth.dto.AuthResponse; // 로그인 응답 (JWT 토큰 포함)
import com.langjoo.prac.auth.dto.LoginRequest; // 로그인 요청 DTO
import com.langjoo.prac.user.dto.UserRegisterRequest; // 회원가입 요청 DTO
import com.langjoo.prac.user.dto.UserProfileResponse; // 프로필 조회 응답 DTO
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

    // 3. 특정 사용자의 프로필 화면으로 이동하여 개인 타임라인 로딩
    // GET /api/users/{username}
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable String username) {

        // Service 계층에서 username을 사용하여 사용자 정보 및 해당 유저의 최신 트윗 목록을 함께 조회
        UserProfileResponse response = userService.getUserProfile(username);
        return ResponseEntity.ok(response);
    }

    // 4. 회원 탈퇴
    // DELETE /api/users
    @DeleteMapping
    public ResponseEntity<Void> deactivateUser(
            @AuthenticationPrincipal UserDetailsImpl currentUser) { // 현재 로그인 사용자 확인

        // Service 계층에서 현재 사용자 ID를 받아 계정 비활성화 또는 삭제 처리
        userService.deactivateUser(currentUser.getUserId());
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}