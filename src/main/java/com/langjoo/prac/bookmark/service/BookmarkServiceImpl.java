package com.langjoo.prac.bookmark.service;

import com.langjoo.prac.bookmark.repository.BookmarkRepository;
import com.langjoo.prac.common.exception.NotFoundException;
import com.langjoo.prac.domain.Bookmark;
import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import com.langjoo.prac.tweet.dto.TweetResponse;
import com.langjoo.prac.tweet.dto.TweetSearchRequest;
import com.langjoo.prac.tweet.repository.TweetRepository;
import com.langjoo.prac.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public boolean toggleBookmark(Long userId, Long tweetId) {
        User user = findUserById(userId);
        Tweet tweet = findTweetById(tweetId);

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserAndTweet(user, tweet);

        if (existingBookmark.isPresent()) {
            // 1. 이미 좋아요를 눌렀다면: 좋아요 취소 (DELETE)
            bookmarkRepository.delete(existingBookmark.get());

            return false; // 좋아요 취소됨

        } else {
            // 1. 좋아요를 누르지 않았다면: 좋아요 생성 (INSERT)
            Bookmark newBookmark = new Bookmark(user, tweet);
            bookmarkRepository.save(newBookmark);

            return true; // 좋아요 생성됨
        }
    }

    // 2. 내가 북마크 해놓은 트윗 중 검색
    @Override
    public List<TweetResponse> searchBookmarkedTweets(Long currentUserId, TweetSearchRequest request) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("검색 키워드 또는 기간이 필요합니다.");
        }

        User currentUser = findUserById(currentUserId); // 유틸리티 메서드 가정

        // -------------------------------------------------------------
        // 📌 [추가] 검색 기간 LocalTime 설정 로직
        // -------------------------------------------------------------
        LocalDateTime since = null;
        if (request.getSince() != null) {
            // 'since' 날짜의 시작 시간 (00:00:00)으로 변환
            since = request.getSince().atStartOfDay();
        }

        LocalDateTime until = null;
        if (request.getUntil() != null) {
            // 'until' 날짜의 종료 시간 (23:59:59.999...)으로 변환
            // JDBC/JPA는 보통 23:59:59.999999999까지 처리할 수 있지만,
            // 안전하게 다음 날의 시작 시간 직전으로 처리하는 것이 일반적입니다.
            // 여기서는 명확성을 위해 23:59:59로 설정합니다.
            until = request.getUntil().atTime(23, 59, 59);
        }
        // -------------------------------------------------------------

        // 📌 [가정] BookmarkRepository에 북마크된 트윗을 조건으로 검색하는 메서드가 있다고 가정
        // List<Tweet> findBookmarkedTweetsByConditions(User user, TweetSearchRequest request);
        List<Tweet> tweets = bookmarkRepository.findBookmarkedTweetsByConditions(
                currentUser,
                request.getKeyword(),
                since, // 변환된 LocalDateTime
                until // 변환된 LocalDateTime
        );

        // 플래그 처리는 여기서도 필요합니다.
        return tweets.stream().map(TweetResponse::from).collect(Collectors.toList());
    }

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
    @Transactional
    public int getBookmarkCount(Long tweetId) {
        return bookmarkRepository.countByTweet_Id(tweetId);
    }

}
