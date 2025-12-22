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
    // 📌 [추가] 내가 이 트윗에 좋아요를 눌렀는지 여부
    private boolean isLikedByMe;

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

    // 📌 [추가] 답글 개수 필드
    private int replyCount;

    // 📌 [추가] 이 트윗이 응답하고 있는 원본 트윗의 ID
    private Long replyToTweetId;

    // 📌 [추가] 이 트윗이 응답하고 있는 원본 트윗 작성자의 username (UI 표시용)
    private String replyToUsername;



    // 📌 엔티티를 DTO로 변환하는 팩토리 메서드 (핵심!)
    public static TweetResponse from(Tweet tweet) {
        // 1. 📌 카운트의 출처(Source)를 결정합니다. 기본값은 현재 트윗입니다.
        Tweet countSource = tweet;

        // 2. [핵심 로직] 순수 리트윗인 경우, 원본 트윗을 카운트의 출처로 지정합니다.
        // tweet.isRetweet() 헬퍼 메서드를 사용하여 리트윗인지 확인합니다.
        if (tweet.isRetweet() && tweet.getRetweetType() == RetweetType.PURE_RETWEET && tweet.getOriginalTweet() != null) {

            // 원본 트윗 엔티티를 카운트 소스로 사용합니다.
            countSource = tweet.getOriginalTweet();
        }

        // 답글 대상 트윗 정보 추출 (null 체크)
        Long replyId = null;
        String replyUsername = null;

        if (tweet.getReplyToTweet() != null) {
            replyId = tweet.getReplyToTweet().getId();
            // Lazy Loading을 피하기 위해 User 엔티티가 Fetch Join 되어 있어야 합니다.
            // (혹은 service 레이어에서 DTO 변환 전 미리 로드해야 합니다.)
            if (tweet.getReplyToTweet().getUser() != null) {
                replyUsername = tweet.getReplyToTweet().getUser().getUsername();
            }
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
                // 4. 📌 [수정] 카운트는 countSource의 것을 사용합니다.
                .likeCount(countSource.getLikeCount())
                .retweetCount(countSource.getRetweetCount())

                // 📌 [추가] 엔티티에서 답글 개수를 가져와 설정
                .replyCount(tweet.getReplyCount())

                // 📌 [추가] 답글 정보 초기화
                .replyToTweetId(replyId)
                .replyToUsername(replyUsername)

                // 📌 추가된 필드 설정
                .type(tweet.getRetweetType()) // 👈 엔티티의 타입을 가져와 설정
                .originalTweetId(tweet.getOriginalTweet() != null ? tweet.getOriginalTweet().getId() : null)

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