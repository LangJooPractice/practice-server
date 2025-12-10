package com.langjoo.prac.tweet.service;

import com.langjoo.prac.tweet.dto.TweetRequest;
import com.langjoo.prac.tweet.dto.TweetResponse;
import com.langjoo.prac.tweet.dto.TweetSearchRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TweetService {

    // 1. 팔로우 트윗 피드 조회 (홈 화면)
    List<TweetResponse> getTimelineFeed(Long currentUserId, Pageable pageable);

    // 2. 트윗 상세 조회
    TweetResponse getTweetById(Long tweetId);

    // 3. 신규 트윗 작성
    TweetResponse createTweet(Long userId, TweetRequest request);

    // 4. 트윗 삭제
    void deleteTweet(Long userId, Long tweetId);

    // 5. 리트윗 또는 인용 트윗 생성
    TweetResponse createRetweet(Long userId, Long originalTweetId, String quoteContent);

    // 6. 리트윗 취소
    void cancelRetweet(Long userId, Long originalTweetId);


    public List<TweetResponse> searchUserTweets(Long currentUserId, String targetUsername, TweetSearchRequest request);
    public List<TweetResponse> searchAllTweets(Long currentUserId, TweetSearchRequest request);
}