package com.langjoo.prac.tweet.repository;

import com.langjoo.prac.domain.RetweetType;
import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    // 📌 1. 특정 User가 작성한 총 트윗 수 계산
    // SELECT COUNT(t) FROM Tweet t WHERE t.user = :profileUser
    long countByUser(User profileUser);

    // Spring Data JPA는 이 메서드 이름으로 'WHERE user_id IN (:userIds)' 쿼리를 자동 생성합니다.
    List<Tweet> findAllByUserIdIn(List<Long> userIds, Pageable pageable);

    // 특정 사용자가 작성한 모든 일반 트윗(리트윗이 아닌 트윗)을 최신순으로 조회
    List<Tweet> findByUserAndRetweetTypeOrderByCreatedAtDesc(User user, RetweetType retweetType);

    // 특정 사용자가 작성한 모든 트윗(일반 트윗 + 리트윗)을 최신순으로 조회
    List<Tweet> findTop20ByUserOrderByCreatedAtDesc(User user);

    // 📌 리트윗 엔티티를 찾는 메서드 추가 (리트윗 취소 시 사용)
    // 리트윗을 한 사용자(User)와 리트윗된 원본 트윗(OriginalTweet)을 기준으로 조회
    Optional<Tweet> findByUserAndOriginalTweet(User user, Tweet originalTweet);

    // 원본 트윗 ID로 해당 트윗을 리트윗한 모든 리트윗 개체를 조회
    List<Tweet> findByOriginalTweetOrderByCreatedAtDesc(Tweet originalTweet);

    // 특정 유저(User)가 특정 원본 트윗(OriginalTweet)에 대해, 특정 타입(RetweetType)을 가진 트윗을 조회합니다.
    Optional<Tweet> findByUserAndOriginalTweetAndRetweetType(
            User user,
            Tweet originalTweet,
            RetweetType retweetType
    );

    // 2. 📌 리트윗 여부를 확인하기 위한 집합 조회 메서드 (새로 추가)
    // 현재 유저가 특정 OriginalTweet ID 목록을 리트윗한 모든 Tweet 레코드를 조회합니다.
    List<Tweet> findByUserAndOriginalTweetIdIn(User user, List<Long> originalTweetIds);

}