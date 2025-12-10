package com.langjoo.prac.like.repository;

import com.langjoo.prac.domain.Like;
import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 사용자가 특정 트윗에 좋아요를 눌렀는지 확인 (좋아요 상태 확인 및 취소 시 사용)
    Optional<Like> findByUserAndTweet(User user, Tweet tweet);

    // 📌 [추가] 특정 유저(user_id)가 타임라인에 존재하는 트윗들 ID(tweet_id)에 좋아요를 눌렀는지 모두 조회
    List<Like> findByUserIdAndTweetIdIn(Long userId, List<Long> tweetIds);

    // 특정 트윗에 좋아요를 누른 모든 Like 관계 목록 조회 (좋아요 누른 사용자 목록)
    List<Like> findByTweet(Tweet tweet);

    // 특정 사용자가 좋아요를 누른 모든 Like 관계 목록 조회 (사용자가 좋아한 트윗 목록)
    List<Like> findByUser(User user);

    // 특정 트윗의 좋아요 개수를 계산 (성능 최적화 전 임시 사용)
    long countByTweet(Tweet tweet);

    // 📌 추가 권장: 좋아요 관계가 존재하는지 여부를 빠르게 확인
    boolean existsByUserAndTweet(User user, Tweet tweet);


}
