package com.langjoo.prac.tweet.controller;

import com.langjoo.prac.auth.config.UserDetailsImpl; // 인증된 사용자 정보를 가정
import com.langjoo.prac.tweet.dto.TweetRequest; // 신규 트윗 작성을 위한 DTO
import com.langjoo.prac.tweet.dto.TweetResponse;
import com.langjoo.prac.tweet.service.TweetService; // Service 계층 주입
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
@RequestMapping
public class TweetController {

    private final TweetService tweetService; // 💡 Service 계층 주입

    // 1. 팔로우하는 사용자들의 트윗 리스트 가져오기 (메인 홈 화면 구성)
    // GET /prac.com/home
    @GetMapping("/home")
    public ResponseEntity<List<TweetResponse>> getTimelineFeed(
            @AuthenticationPrincipal UserDetailsImpl currentUser, // 현재 로그인 사용자
            @PageableDefault(size = 20) Pageable pageable) { // 페이지네이션 정보

        // Service 계층에서 팔로우 목록 기반으로 피드 조회
        List<TweetResponse> feed = tweetService.getTimelineFeed(currentUser.getUserId(), pageable);
        return ResponseEntity.ok(feed);
    }


    // 2. 트윗 하나 자세하게 띄우기
    // GET /prac.com/{username}/status/{tweetId}
    @GetMapping("/{username}/status/{tweetId}")
    public ResponseEntity<TweetResponse> getTweetDetail(
            @PathVariable String username,
            @PathVariable Long tweetId) {

        // Service 계층에서 username을 검증하며 트윗 상세 조회
        TweetResponse response = tweetService.getTweetByUsernameAndId(username, tweetId);
        return ResponseEntity.ok(response);
    }


    // 3. 신규 트윗 작성
    // POST /prac.com/tweets
    @PostMapping("/tweets")
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
    public ResponseEntity<Void> cancelRetweet(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long originalTweetId) {

        // Service 계층에서 리트윗 엔티티를 찾아서 삭제
        tweetService.cancelRetweet(currentUser.getUserId(), originalTweetId);
        return ResponseEntity.noContent().build();
    }
}