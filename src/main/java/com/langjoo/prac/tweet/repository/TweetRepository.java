package com.langjoo.prac.tweet.repository;

import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    // 특정 사용자가 작성한 모든 일반 트윗(리트윗이 아닌 트윗)을 최신순으로 조회
    List<Tweet> findByUserAndIsRetweetFalseOrderByCreatedAtDesc(User user);

    // 특정 사용자가 작성한 모든 트윗(일반 트윗 + 리트윗)을 최신순으로 조회
    List<Tweet> findTop20ByUserOrderByCreatedAtDesc(User user);

    // 📌 리트윗 엔티티를 찾는 메서드 추가 (리트윗 취소 시 사용)
    // 리트윗을 한 사용자(User)와 리트윗된 원본 트윗(OriginalTweet)을 기준으로 조회
    Optional<Tweet> findByUserAndOriginalTweet(User user, Tweet originalTweet);

    // 원본 트윗 ID로 해당 트윗을 리트윗한 모든 리트윗 개체를 조회
    List<Tweet> findByOriginalTweetOrderByCreatedAtDesc(Tweet originalTweet);

}