package com.langjoo.prac.bookmark.repository;

import com.langjoo.prac.domain.Bookmark;
import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 📌 유일성 검증 및 삭제를 위한 조회 (핵심)
    Optional<Bookmark> findByUserAndTweet(User user, Tweet tweet);

    // 📌 특정 트윗 ID를 가진 북마크의 개수를 세는 쿼리 메서드
    // Tweet 엔티티 내의 id 필드를 참조하므로 Tweet_Id 형식을 사용합니다.
    int countByTweet_Id(Long tweetId);

    // 📌 특정 유저의 북마크 목록 조회 (북마크 페이지 로딩 시 사용)
    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);

    // 📌 [추가] 북마크된 트윗을 조건에 맞게 검색하는 JPQL 쿼리
    // Bookmark 엔티티를 조회하지만, 반환 타입은 List<Tweet>으로 설정하여 북마크된 트윗 자체를 반환합니다.
    @Query("SELECT b.tweet FROM Bookmark b " +
            "WHERE b.user = :user " + // 1. 현재 사용자(북마크를 한 사람)
            "AND (:keyword IS NULL OR :keyword = '' OR b.tweet.content LIKE %:keyword%) " + // 2. 키워드 검색
            "AND (:since IS NULL OR b.tweet.createdAt >= :since) " + // 3. 시작 시점
            "AND (:until IS NULL OR b.tweet.createdAt <= :until) " + // 4. 종료 시점
            "ORDER BY b.tweet.createdAt DESC")
    List<Tweet> findBookmarkedTweetsByConditions(
            @Param("user") User user,
            @Param("keyword") String keyword,
            @Param("since") LocalDateTime since,
            @Param("until") LocalDateTime until);
}