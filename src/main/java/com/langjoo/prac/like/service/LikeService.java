package com.langjoo.prac.like.service;

public interface LikeService {
    // 좋아요 생성/취소 (토글)
    boolean toggleLike(Long userId, Long tweetId);
    public int getLikeCount(Long tweetId);

}
