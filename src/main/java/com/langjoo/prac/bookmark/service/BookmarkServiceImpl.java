package com.langjoo.prac.bookmark.service;

import com.langjoo.prac.bookmark.repository.BookmarkRepository;
import com.langjoo.prac.common.exception.NotFoundException;
import com.langjoo.prac.domain.Bookmark;
import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import com.langjoo.prac.tweet.repository.TweetRepository;
import com.langjoo.prac.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
