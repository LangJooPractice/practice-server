package com.langjoo.prac.bookmark.controller;

import com.langjoo.prac.auth.config.UserDetailsImpl;
import com.langjoo.prac.bookmark.service.BookmarkService;
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
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    // POST /tweets/{tweetId}/bookmark
    @PostMapping("/{tweetId}/bookmark")
    public ResponseEntity<Map<String, Boolean>> toggleBookmark(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long tweetId) {
        // 좋아요 생성/취소 후 현재 상태(true/false)를 반환
        boolean isBookmarked = bookmarkService.toggleBookmark(currentUser.getUserId(), tweetId);

        // 200 OK와 함께 현재 상태를 JSON으로 반환
        return ResponseEntity.ok(Map.of("isBookmarked", isBookmarked));
    }
}
