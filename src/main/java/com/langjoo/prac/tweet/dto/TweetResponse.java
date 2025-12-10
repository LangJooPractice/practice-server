package com.langjoo.prac.tweet.dto; // 📌 권장 패키지 위치

import com.langjoo.prac.domain.RetweetType;
import com.langjoo.prac.domain.Tweet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder // Lombok Builder 패턴으로 쉽게 객체 생성
public class TweetResponse {

    private RetweetType type; // 👈 Enum 타입 추가


    // 📌 새로운 필드: 현재 로그인 유저가 이 트윗(원본이든 리트윗이든)을 리트윗 했는지 여부
    private boolean isRetweetedByMe; // 👈 추가

    // 📌 2. 원본 트윗의 ID 추가 (리트윗일 경우에만 값이 존재)
    // Long 타입은 null을 가질 수 있으므로, 원본 트윗이 아닐 때는 null이 됩니다.
    private Long originalTweetId;

    private Long tweetId;
    private String content;
    private LocalDateTime createdAt;

    // 작성자 정보 (User 엔티티에서 필요한 정보만 추출)
    private Long userId;
    private String username; // 노출되는 아이디 (LoginId 또는 name)
    private String nickname;
    //private String profileImageUrl;

    // 좋아요, 리트윗 카운트
    private int likeCount;
    private int retweetCount;

    // 📌 엔티티를 DTO로 변환하는 팩토리 메서드 (핵심!)
    public static TweetResponse from(Tweet tweet) {
        // ⚠️ 주의: 리트윗의 원본 트윗 ID를 가져오는 방식 확인
        Long originalId = null;
        if (tweet.isRetweet() && tweet.getOriginalTweet() != null) {
            originalId = tweet.getOriginalTweet().getId();
        }

        return TweetResponse.builder()
                .tweetId(tweet.getId())
                .content(tweet.getContent())
                .createdAt(tweet.getCreatedAt())
                // User 엔티티에서 필요한 정보만 가져와 노출
                .userId(tweet.getUser().getId())
                .username(tweet.getUser().getUsername()) // DB name 필드를 사용한다고 가정
                .nickname(tweet.getUser().getNickname())
                // 카운트 필드
                .likeCount(tweet.getLikeCount())
                .retweetCount(tweet.getRetweetCount())

                // 📌 추가된 필드 설정
                .type(tweet.getRetweetType()) // 👈 엔티티의 타입을 가져와 설정
                .originalTweetId(originalId) // 위에서 추출한 원본 트윗 ID를 설정

                .build();
    }

    // 📌 [추가] isRetweetedByMe 값을 직접 설정하는 팩토리 메서드
    public static TweetResponse from(Tweet tweet, boolean isRetweetedByMe) {
        // 💡 기존 from(Tweet tweet)을 호출하여 기본 정보를 채웁니다.
        TweetResponse response = from(tweet);

        // 💡 플래그만 오버라이드합니다.
        response.setRetweetedByMe(isRetweetedByMe);

        return response;
    }
}