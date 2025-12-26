package com.langjoo.prac.bookmark.controller;

import com.langjoo.prac.auth.config.UserDetailsImpl;
import com.langjoo.prac.bookmark.dto.BookmarkToggleResponse;
import com.langjoo.prac.bookmark.service.BookmarkService;
import com.langjoo.prac.tweet.dto.TweetResponse;
import com.langjoo.prac.tweet.dto.TweetSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "북마크", description = "북마크 등록/취소 + 검색")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    // POST /tweets/{tweetId}/bookmark
    @PostMapping("/tweets/{tweetId}/bookmark")
    @Operation(summary = "북마크 등록/취소", description = "처음 보내면 북마크 등록 되고 북마크 상태였다면 북마크 취소됨")
    public ResponseEntity<BookmarkToggleResponse> toggleBookmark(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long tweetId) {
        // 좋아요 생성/취소 후 현재 상태(true/false)를 반환
        boolean isBookmarked = bookmarkService.toggleBookmark(currentUser.getUserId(), tweetId);

        int currentBookmarkCount = bookmarkService.getBookmarkCount(tweetId); // 필요시 추가

        // 2. DTO 생성 및 반환
        BookmarkToggleResponse response = BookmarkToggleResponse.builder()
                .tweetId(tweetId)
                .isBookmarked(isBookmarked)
                .bookmarkCount(currentBookmarkCount)
                .build();

        return ResponseEntity.ok(response);

    }

    // 2. 내가 북마크 해놓은 트윗 중 검색
// GET /api/tweets/bookmarks/search?keyword=...&since=...
    @GetMapping("/search/bookmarks")
    @Operation(summary = "북마크 해놓은 트윗 중 검색", description = "키워드 검색&작성 시기 검색 가능")
    public ResponseEntity<List<TweetResponse>> searchBookmarkedTweets(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @ModelAttribute TweetSearchRequest request) {

        // 현재 로그인 유저의 ID를 사용하여 북마크 트윗 검색
        List<TweetResponse> results = bookmarkService.searchBookmarkedTweets(currentUser.getUserId(), request);
        return ResponseEntity.ok(results);
    }
}
