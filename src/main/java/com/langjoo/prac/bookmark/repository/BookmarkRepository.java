package com.langjoo.prac.bookmark.repository;

import com.langjoo.prac.domain.Bookmark;
import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 📌 유일성 검증 및 삭제를 위한 조회 (핵심)
    Optional<Bookmark> findByUserAndTweet(User user, Tweet tweet);

    // 📌 특정 유저의 북마크 목록 조회 (북마크 페이지 로딩 시 사용)
    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);
}