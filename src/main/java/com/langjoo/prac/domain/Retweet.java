package com.langjoo.prac.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Retweet extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rt_id")
    private Long id;

    // 📌 트윗 작성자 (리트윗을 한 사용자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 트윗 본문 (리트윗 시에는 인용 텍스트만 저장되므로 null 허용)
    @Column(length = 280)
    private String content;


    // ---------------------- 📌 리트윗 관련 필드 ----------------------

    // 1. 리트윗 여부 플래그
    @Column(nullable = false)
    private boolean isRetweet = false; // 기본값은 일반 트윗(false)

    // 2. 리트윗한 원본 트윗 참조
    // 이 필드가 null이 아니면 이 엔티티는 리트윗임
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_tweet_id") // DB 외래키 컬럼 이름
    private Tweet originalTweet;

    // -------------------------------------------------------------

    // 💡 리트윗 팩토리 메서드
    public static Tweet createRetweet(User user, Tweet originalTweet, String quoteContent) {
        Tweet retweet = new Tweet();
        retweet.setUser(user); // 리트윗을 한 사용자
        retweet.setContent(quoteContent); // 인용 텍스트 (null일 수 있음)
        retweet.setOriginalTweet(originalTweet); // 원본 트윗 연결
        retweet.setRetweet(true); // 리트윗 플래그 설정

        // 📌 (선택적) 여기서 originalTweet의 retweetCount를 +1 하는 로직을 Service 계층에서 구현해야 함

        return retweet;
    }


}
