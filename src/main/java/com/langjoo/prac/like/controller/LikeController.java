package com.langjoo.prac.like.controller;


import com.langjoo.prac.auth.config.UserDetailsImpl;
import com.langjoo.prac.like.dto.LikeToggleResponse;
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

    @PostMapping("/{tweetId}/like")
    @Operation(summary = "좋아요 생성/취소", description = "처음 호출 시 좋아요, 다시 호출 시 취소합니다.")
    public ResponseEntity<LikeToggleResponse> toggleLike( // 📌 반환 타입 변경
                                                          @AuthenticationPrincipal UserDetailsImpl currentUser,
                                                          @PathVariable Long tweetId) {

        // 1. 서비스 로직 실행 (상태와 카운트를 받아온다고 가정)
        boolean isLiked = likeService.toggleLike(currentUser.getUserId(), tweetId);
        int currentLikeCount = likeService.getLikeCount(tweetId); // 필요시 추가

        // 2. DTO 생성 및 반환
        LikeToggleResponse response = LikeToggleResponse.builder()
                .tweetId(tweetId)
                .isLiked(isLiked)
                .likeCount(currentLikeCount)
                .build();

        return ResponseEntity.ok(response);
    }
}




// 특정 유저가 좋아요 누른 트윗 모아보기