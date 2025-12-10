package com.langjoo.prac.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes",
        // 📌 user_id와 tweet_id 쌍은 유일해야 함 (중복 좋아요 방지)
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "tweet_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용 시 기본 생성자 필수
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    // 📌 좋아요를 누른 사용자 (ManyToOne 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // DB 외래키 컬럼 이름
    private User user; // 좋아요를 누른 User 객체

    // 📌 좋아요를 받은 트윗 (ManyToOne 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false) // DB 외래키 컬럼 이름
    private Tweet tweet; // 좋아요를 받은 Tweet 객체

    // 💡 생성자
    public Like(User user, Tweet tweet) {
        this.user = user;
        this.tweet = tweet;
    }

}
