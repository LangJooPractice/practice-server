package com.langjoo.prac.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 👈 JPA 사용을 위해 접근제어자 PROTECTED로 설정 (권장)
public class Follow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팔로우를 하는 사용자 (Follower)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // 팔로우를 받는 사용자 (Following)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;


    // 📌 생성자 추가: follower와 following 필드만 받음
    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
        // BaseEntity를 상속받았다면 createdAt은 자동으로 설정됨
    }

}
