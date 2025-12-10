package com.langjoo.prac.bookmark.service;

import com.langjoo.prac.domain.Tweet;

public interface BookmarkService {
    // 북마크 생성/취소 토글
    boolean toggleBookmark(Long userId, Long tweetId);
}
