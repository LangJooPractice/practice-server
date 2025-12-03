package com.langjoo.prac.tweet.dto; // 📌 권장 패키지 위치

import com.langjoo.prac.domain.Tweet;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder // Lombok Builder 패턴으로 쉽게 객체 생성
public class TweetResponse {

    private Long tweetId;
    private String content;
    private LocalDateTime createdAt;

    // 작성자 정보 (User 엔티티에서 필요한 정보만 추출)
    private Long userId;
    private String username; // 노출되는 아이디 (LoginId 또는 name)

    // 좋아요, 리트윗 카운트
    private int likeCount;
    private int retweetCount;

    // 📌 엔티티를 DTO로 변환하는 팩토리 메서드 (핵심!)
    public static TweetResponse from(Tweet tweet) {
        return TweetResponse.builder()
                .tweetId(tweet.getId())
                .content(tweet.getContent())
                .createdAt(tweet.getCreatedAt())
                // User 엔티티에서 필요한 정보만 가져와 노출
                .userId(tweet.getUser().getId())
                .username(tweet.getUser().getUsername()) // DB name 필드를 사용한다고 가정
                .username(tweet.getUser().getNickname())
                // 카운트 필드
                .likeCount(tweet.getLikeCount())
                .retweetCount(tweet.getRetweetCount())
                .build();
    }
}