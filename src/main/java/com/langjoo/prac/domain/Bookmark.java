package com.langjoo.prac.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookmarks",
        uniqueConstraints = { // 📌 유일성 제약 조건: 한 유저는 한 트윗에 한 번만 북마크 가능
                @UniqueConstraint(columnNames = {"user_id", "tweet_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 북마크를 누른 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 북마크를 받은 트윗 (카운트 업데이트 대상)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;


    // 💡 생성자
    public Bookmark(User user, Tweet tweet) {
        this.user = user;
        this.tweet = tweet;
    }
}
