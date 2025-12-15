package com.langjoo.prac.like.controller;


import com.langjoo.prac.auth.config.UserDetailsImpl;
import com.langjoo.prac.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/tweets")
@Tag(name = "좋아요")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // POST /tweets/{tweetId}/like
    @PostMapping("/{tweetId}/like")
    @Operation(summary = "좋아요 생성/취소", description = "처음 보내면 좋아요 생성, 좋아요 상태였다면 좋아요 취소")
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long tweetId) {

        // 좋아요 생성/취소 후 현재 상태(true/false)를 반환
        boolean isLiked = likeService.toggleLike(currentUser.getUserId(), tweetId);

        // 200 OK와 함께 현재 상태를 JSON으로 반환
        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }
}




// 특정 유저가 좋아요 누른 트윗 모아보기