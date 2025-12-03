package com.langjoo.prac.follow.service;

import com.langjoo.prac.domain.Follow;
import com.langjoo.prac.domain.User;
import com.langjoo.prac.follow.repository.FollowRepository;
import com.langjoo.prac.user.repository.UserRepository;
import com.langjoo.prac.common.exception.DuplicateException;
import com.langjoo.prac.common.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // 유틸리티 메서드: ID로 사용자 엔티티를 찾는 메서드
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));
    }

    // 유틸리티 메서드: Username으로 사용자 엔티티를 찾는 메서드
    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("사용자 @" + username + "을(를) 찾을 수 없습니다."));
    }

    // -------------------------------------------------------------
    // 1. 팔로우 관계 생성 (팔로우 하기)
    // -------------------------------------------------------------
    @Override
    public void follow(Long followerId, String followingUsername) {

        // 1. 팔로우 하는 사람(Follower)과 받는 사람(Following) 엔티티 조회
        User follower = findUserById(followerId);
        User following = findUserByUsername(followingUsername);

        // 2. 자기 자신을 팔로우하는지 검증
        if (follower.getId().equals(following.getId())) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        // 3. 이미 팔로우 중인지 검증
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new DuplicateException("이미 팔로우 중인 사용자입니다.");
        }

        // 4. Follow 엔티티 생성 및 저장
        Follow follow = new Follow(follower, following);
        followRepository.save(follow);

        // 📌 추가 로직: User 엔티티의 followerCount/followingCount 증가 (트랜잭션 내에서 처리)
        // following.setFollowerCount(following.getFollowerCount() + 1);
        // follower.setFollowingCount(follower.getFollowingCount() + 1);
        // userRepository.save(following); // JPA Dirty Checking으로 생략 가능
    }

    // -------------------------------------------------------------
    // 2. 팔로우 관계 삭제 (언팔로우 하기)
    // -------------------------------------------------------------
    @Override
    public void unfollow(Long followerId, String followingUsername) {

        // 1. 팔로우 하는 사람(Follower)과 받는 사람(Following) 엔티티 조회
        User follower = findUserById(followerId);
        User following = findUserByUsername(followingUsername);

        // 2. 삭제할 Follow 관계 조회
        Follow followRelation = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 팔로우 관계입니다."));

        // 3. 삭제
        followRepository.delete(followRelation);

        // 📌 추가 로직: User 엔티티의 followerCount/followingCount 감소
        // following.setFollowerCount(following.getFollowerCount() - 1);
        // follower.setFollowingCount(follower.getFollowingCount() - 1);
        // userRepository.save(following); // JPA Dirty Checking으로 생략 가능
    }
}