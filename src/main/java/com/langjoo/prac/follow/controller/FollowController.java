package com.langjoo.prac.follow.controller;

import com.langjoo.prac.auth.config.UserDetailsImpl; // 현재 로그인된 사용자 정보
import com.langjoo.prac.follow.service.FollowService; // Follow 비즈니스 로직
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users") // 사용자 관련 API 경로를 루트로 설정
public class FollowController {

    private final FollowService followService;

    // 1. 특정 유저 팔로우 하기
    // POST /api/users/{followingUsername}/follow
    // 'follower'는 현재 로그인한 유저, 'following'은 경로에 지정된 유저입니다.
    @PostMapping("/{followingUsername}/follow")
    public ResponseEntity<Void> followUser(
            // 팔로우를 요청한 현재 로그인 유저 (Follower)
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            // 팔로우 대상 유저의 @아이디 (Following)
            @PathVariable String followingUsername) {

        // Service 계층으로 팔로우 로직 위임
        followService.follow(currentUser.getUserId(), followingUsername);

        // 201 Created 반환 (리소스 생성 성공)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 2. 특정 유저 언팔로우 하기
    // DELETE /api/users/{followingUsername}/follow
    @DeleteMapping("/{followingUsername}/follow")
    public ResponseEntity<Void> unfollowUser(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable String followingUsername) {

        // Service 계층으로 언팔로우 로직 위임
        followService.unfollow(currentUser.getUserId(), followingUsername);

        // 204 No Content 반환 (리소스 삭제 성공)
        return ResponseEntity.noContent().build();
    }
}