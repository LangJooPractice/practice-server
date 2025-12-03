package com.langjoo.prac.follow.service;

public interface FollowService {

    // 1. 팔로우 관계 생성 (특정 유저 팔로우 하기)
    void follow(Long followerId, String followingUsername);

    // 2. 팔로우 관계 삭제 (특정 유저 언팔로우 하기)
    void unfollow(Long followerId, String followingUsername);
}