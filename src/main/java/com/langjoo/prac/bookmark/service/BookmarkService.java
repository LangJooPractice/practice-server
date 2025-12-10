package com.langjoo.prac.bookmark.service;

import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.tweet.dto.TweetResponse;
import com.langjoo.prac.tweet.dto.TweetSearchRequest;

import java.util.List;

public interface BookmarkService {
    // 북마크 생성/취소 토글
    boolean toggleBookmark(Long userId, Long tweetId);
    public List<TweetResponse> searchBookmarkedTweets(Long currentUserId, TweetSearchRequest request);
}
