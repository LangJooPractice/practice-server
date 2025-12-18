package com.langjoo.prac.like.service;

import com.langjoo.prac.common.exception.NotFoundException;
import com.langjoo.prac.domain.Like;
import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import com.langjoo.prac.like.repository.LikeRepository;
import com.langjoo.prac.tweet.repository.TweetRepository;
import com.langjoo.prac.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository; // User 엔티티 조회용
    private final TweetRepository tweetRepository; // Tweet 엔티티 조회용

    // -------------------------------------------------------------
    // 📌 [추가] 유틸리티 메서드: ID로 User를 찾거나 예외를 던짐
    // -------------------------------------------------------------
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다: ID " + userId));
    }

    // -------------------------------------------------------------
    // 📌 [추가] 유틸리티 메서드: ID로 Tweet을 찾거나 예외를 던짐
    // -------------------------------------------------------------
    private Tweet findTweetById(Long tweetId) {
        return tweetRepository.findById(tweetId)
                .orElseThrow(() -> new NotFoundException("트윗을 찾을 수 없습니다: ID " + tweetId));
    }

    @Override
    public boolean toggleLike(Long userId, Long tweetId) {
        User user = findUserById(userId);
        Tweet tweet = findTweetById(tweetId);

        Optional<Like> existingLike = likeRepository.findByUserAndTweet(user, tweet);

        if (existingLike.isPresent()) {
            // 1. 이미 좋아요를 눌렀다면: 좋아요 취소 (DELETE)
            likeRepository.delete(existingLike.get());

            // 2. 트윗 카운트 감소
            tweet.setLikeCount(tweet.getLikeCount() - 1);
            tweetRepository.save(tweet); // 카운트 업데이트

            return false; // 좋아요 취소됨

        } else {
            // 1. 좋아요를 누르지 않았다면: 좋아요 생성 (INSERT)
            Like newLike = new Like(user, tweet);
            likeRepository.save(newLike);

            // 2. 트윗 카운트 증가
            tweet.setLikeCount(tweet.getLikeCount() + 1);
            tweetRepository.save(tweet); // 카운트 업데이트

            return true; // 좋아요 생성됨
        }
    }

    // 📌 [추가] 현재 좋아요 개수를 가져오는 별도 메서드
    @Override
    @Transactional
    public int getLikeCount(Long tweetId) {
        return likeRepository.countByTweet_Id(tweetId);
    }

}