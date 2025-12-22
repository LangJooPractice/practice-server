package com.langjoo.prac.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tweets")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tweet extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tweet_id")
    private Long id;

    // 사용자(User) 외래 키 매핑 부분
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 280)
    private String content;

    @Column(name = "rt_count")
    private int retweetCount; // int 타입 유지 (기본값 0)

    @Column(name = "like_count")
    private int likeCount; // int 타입 유지 (기본값 0)

    @Column(name = "reply_count")
    private int replyCount = 0; // 📌 답글 개수 필드 추가 (기본값 0)

    // 📌 [수정] boolean isRetweet 대신 RetweetType Enum 사용
    @Enumerated(EnumType.STRING) // DB에 문자열로 저장
    @Column(name = "retweet_type", nullable = false)
    private RetweetType retweetType = RetweetType.ORIGINAL; // 기본값은 ORIGINAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_tweet_id")
    private Tweet originalTweet;


    // 📌 [추가] 이 트윗이 어떤 트윗에 대한 응답(답글)인지 나타냄 (대화 구조의 핵심)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_tweet_id") // 컬럼 이름을 reply_to_tweet_id로 지정
    private Tweet replyToTweet;


//    // 💡 [수정] 일반 트윗 생성자: RetweetType.ORIGINAL로 설정
//    public Tweet(User user, String content) {
//        this.user = user;
//        this.content = content;
//        this.retweetType = RetweetType.ORIGINAL; // 👈 타입 설정
//        this.originalTweet = null;
//    }

    // 💡 새로운 트윗 생성자 업데이트 (답글 기능 포함)
// 답글이 아닌 일반 트윗일 경우 replyToTweet은 null입니다.
    public Tweet(User user, String content, Tweet replyToTweet, RetweetType type, Tweet originalTweet) {
        this.user = user;
        this.content = content;
        this.replyToTweet = replyToTweet; // 📌 추가된 필드 초기화
        this.retweetType = type;
        this.originalTweet = originalTweet;
        this.likeCount = 0;
        this.retweetCount = 0;
    }

    // 💡 [추가] 리트윗 여부를 확인하는 헬퍼 메서드 (기존 isRetweet()의 역할 대체)
    public boolean isRetweet() {
        return this.retweetType != RetweetType.ORIGINAL;
    }


    // 📌 개수 조절 메서드
    public void incrementReplyCount() { this.replyCount++; }
    public void decrementReplyCount() {
        if (this.replyCount > 0) { // 0 미만으로 내려가지 않도록 방어 로직
            this.replyCount--;
        }
    }

    // 💡 [수정] 리트윗 팩토리 메서드: content와 type을 분리하여 생성
    public static Tweet createRetweet(User user, Tweet originalTweet, String quoteContent, RetweetType type) {
        Tweet retweet = new Tweet();
        retweet.setUser(user);

        // 순수 리트윗(PURE_RETWEET)의 경우 quoteContent는 ""가 됩니다.
        String contentToSave = (quoteContent != null && !quoteContent.trim().isEmpty())
                ? quoteContent
                : "";

        retweet.setContent(contentToSave);
        retweet.setOriginalTweet(originalTweet);

        // 📌 타입 설정
        retweet.setRetweetType(type); // 👈 외부에서 PURE 또는 QUOTE 타입을 받아 설정

        // likeCount, retweetCount는 int 타입 기본값 0으로 자동 설정

        return retweet;
    }
}