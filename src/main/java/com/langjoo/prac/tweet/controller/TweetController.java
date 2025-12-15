package com.langjoo.prac.tweet.controller;

import com.langjoo.prac.auth.config.UserDetailsImpl; // 인증된 사용자 정보를 가정
import com.langjoo.prac.tweet.dto.TweetRequest; // 신규 트윗 작성을 위한 DTO
import com.langjoo.prac.tweet.dto.TweetResponse;
import com.langjoo.prac.tweet.dto.TweetSearchRequest;
import com.langjoo.prac.tweet.service.TweetService; // Service 계층 주입
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // 요청 DTO 유효성 검사를 위한 import
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable; // 페이지네이션 처리를 위한 import
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "트윗 관련", description = "트윗 CRUD + 검색 기능")
public class TweetController {

    private final TweetService tweetService; // 💡 Service 계층 주입

    // 1. 팔로우하는 사용자들의 트윗 리스트 가져오기 (메인 홈 화면 구성)
    // GET /prac.com/home
    @GetMapping("/home")
    @Operation(summary = "타임라인 불러오기", description = "팔로우 중인 유저들의 트윗을 불러와 홈 화면 구성")
    public ResponseEntity<List<TweetResponse>> getTimelineFeed(
            @AuthenticationPrincipal UserDetailsImpl currentUser, // 현재 로그인 사용자
            @PageableDefault(size = 20) Pageable pageable) { // 페이지네이션 정보

        // Service 계층에서 팔로우 목록 기반으로 피드 조회
        List<TweetResponse> feed = tweetService.getTimelineFeed(currentUser.getUserId(), pageable);
        return ResponseEntity.ok(feed);
    }


    // 2. 트윗 하나 자세하게 띄우기
    // GET /prac.com/tweets/{tweetId}
    @GetMapping("/tweets/{tweetId}")
    @Operation(summary = "트윗 상세 조회", description = "트윗 내용을 상세 조회합니다")
    public ResponseEntity<TweetResponse> getTweetDetail(
            @PathVariable Long tweetId) {

        // Service 계층에서 username을 검증하며 트윗 상세 조회
        TweetResponse response = tweetService.getTweetById(tweetId);
        return ResponseEntity.ok(response);
    }


    // 3. 신규 트윗 작성
    // POST /prac.com/tweets
    @PostMapping("/tweets")
    @Operation(summary = "신규 트윗 작성", description = "replyToTweetId는 특정 트윗에 답글을 작성할 때 원 트윗 id 입력하면 됨, 그게 아니면 사용 X")
    public ResponseEntity<TweetResponse> createNewTweet(
            @AuthenticationPrincipal UserDetailsImpl currentUser, // 현재 로그인 사용자
            @Valid @RequestBody TweetRequest request) { // 트윗 내용 (Validation 필요)

        // Service 계층에서 트윗 생성 및 저장
        TweetResponse response = tweetService.createTweet(currentUser.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 4. 트윗 삭제하기
    // DELETE /prac.com/tweets/{tweetId}
    @DeleteMapping("/tweets/{tweetId}")
    @Operation(summary = "트윗 삭제")
    public ResponseEntity<Void> deleteTweet(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long tweetId) {

        // Service 계층에서 사용자 권한 확인 후 트윗 삭제
        tweetService.deleteTweet(currentUser.getUserId(), tweetId);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }


    // 5. 리트윗&인용하기
    // POST /prac.com/tweets/{originalTweetId}/retweet
    @PostMapping("/tweets/{originalTweetId}/retweet")
    @Operation(summary = "리트윗&인용", description = "content가 있으면 인용트윗, 없으면 순수 리트윗")
    public ResponseEntity<TweetResponse> handleRetweet(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long originalTweetId,
            @RequestBody(required = false) TweetRequest request) { // 인용 트윗 본문 (선택적)

        // Service 계층에서 리트윗 또는 인용 트윗 생성
        TweetResponse response = tweetService.createRetweet(
                currentUser.getUserId(),
                originalTweetId,
                request != null ? request.getContent() : null);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 6. 리트윗 취소하기
    // DELETE /prac.com/tweets/{originalTweetId}/retweet
    @DeleteMapping("/tweets/{originalTweetId}/retweet")
    @Operation(summary = "리트윗 취소", description = "순수 리트윗 취소만. 인용트윗은 트윗 삭제 기능 이용")
    public ResponseEntity<Void> cancelRetweet(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long originalTweetId) {

        // Service 계층에서 리트윗 엔티티를 찾아서 삭제
        tweetService.cancelRetweet(currentUser.getUserId(), originalTweetId);
        return ResponseEntity.noContent().build();
    }

    // 1. 존재하는 전체 트윗 중 검색
// GET /api/tweets/search/all?keyword=...&since=...
    @GetMapping("/search/all")
    @Operation(summary = "전체 트윗 중 검색", description = "키워드 검색&작성 시기 검색 가능")
    public ResponseEntity<List<TweetResponse>> searchAllTweets(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @ModelAttribute TweetSearchRequest request) {

        List<TweetResponse> results = tweetService.searchAllTweets(currentUser.getUserId(), request);
        return ResponseEntity.ok(results);
    }

    // 3. 특정 유저의 트윗 중 검색
// 📌 [수정] 경로 변수를 {username}으로 변경
// GET /api/users/{username}/tweets/search?keyword=...&since=...
    @GetMapping("/search/users/{username}")
    @Operation(summary = "특정 유저의 트윗 중 검색", description = "키워드 검색&작성 시기 검색 가능")
    public ResponseEntity<List<TweetResponse>> searchUserTweets(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            // 📌 [수정] Long targetUserId 대신 String targetUsername으로 변경
            @PathVariable String username,
            @ModelAttribute TweetSearchRequest request) {

        // 📌 [수정] 서비스 호출 시 targetUsername을 전달
        List<TweetResponse> results = tweetService.searchUserTweets(currentUser.getUserId(), username, request);
        return ResponseEntity.ok(results);
    }
}