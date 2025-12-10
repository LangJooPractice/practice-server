package com.langjoo.prac.bookmark.controller;

import com.langjoo.prac.auth.config.UserDetailsImpl;
import com.langjoo.prac.bookmark.service.BookmarkService;
import com.langjoo.prac.tweet.dto.TweetResponse;
import com.langjoo.prac.tweet.dto.TweetSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    // POST /tweets/{tweetId}/bookmark
    @PostMapping("/tweets/{tweetId}/bookmark")
    public ResponseEntity<Map<String, Boolean>> toggleBookmark(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long tweetId) {
        // 좋아요 생성/취소 후 현재 상태(true/false)를 반환
        boolean isBookmarked = bookmarkService.toggleBookmark(currentUser.getUserId(), tweetId);

        // 200 OK와 함께 현재 상태를 JSON으로 반환
        return ResponseEntity.ok(Map.of("isBookmarked", isBookmarked));
    }

    // 2. 내가 북마크 해놓은 트윗 중 검색
// GET /api/tweets/bookmarks/search?keyword=...&since=...
    @GetMapping("/search/bookmarks")
    public ResponseEntity<List<TweetResponse>> searchBookmarkedTweets(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @ModelAttribute TweetSearchRequest request) {

        // 현재 로그인 유저의 ID를 사용하여 북마크 트윗 검색
        List<TweetResponse> results = bookmarkService.searchBookmarkedTweets(currentUser.getUserId(), request);
        return ResponseEntity.ok(results);
    }
}
