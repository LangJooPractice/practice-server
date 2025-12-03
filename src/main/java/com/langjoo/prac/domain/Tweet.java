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

    // 📌 사용자(User) 외래 키 매핑 부분
    @ManyToOne(fetch = FetchType.LAZY) // Tweet(N) : User(1) 관계 정의
    @JoinColumn(name = "user_id", nullable = false) // 실제 DB 컬럼 이름을 'user_id'로 지정
    private User user; // JPA가 이 필드를 통해 User 엔티티 전체를 관리

    @Column(nullable = false, length = 280) // NOT NULL 제약조건과 최대 길이 280 지정
    private String content;

    @Column(name = "rt_count")
    private Integer retweetCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "is_retweet")
    private boolean isRetweet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_tweet_id")
    private Tweet originalTweet;

    // 💡 일반 트윗 생성자: 클래스 이름과 동일하게, 반환 타입(void 등)은 삭제해야 합니다.
    public Tweet(User user, String content) {
        this.user = user;
        this.content = content;
        this.isRetweet = false;
        this.originalTweet = null;
    }

    // 💡 리트윗 팩토리 메서드 추가
    public static Tweet createRetweet(User user, Tweet originalTweet, String quoteContent) {
        Tweet retweet = new Tweet();
        retweet.setUser(user);
        retweet.setContent(quoteContent);
        retweet.setOriginalTweet(originalTweet);
        retweet.setRetweet(true); // isRetweet 필드가 true로 설정됨
        return retweet;
    }
}
