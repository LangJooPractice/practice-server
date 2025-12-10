package com.langjoo.prac.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    // 2. 📌 공개된 고유 ID (트위터의 @아이디 역할)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // 3. 📌 닉네임 (표시 이름) - 중복 허용
    @Column(name = "nickname", nullable = false)
    private String nickname; // 👈 닉네임 필드 추가

    @Column(name = "user_pw", nullable = false)
    private String password;


    // 📌 Follow 엔티티와의 관계 추가

    // 자신이 '팔로우'하는 목록 (내가 follower)
    // mappedBy = "follower"는 Follow 엔티티의 'follower' 필드에 의해 매핑됨을 의미
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followings = new HashSet<>();

    // 자신을 '팔로우'하는 목록 (내가 following)
    // mappedBy = "following"은 Follow 엔티티의 'following' 필드에 의해 매핑됨을 의미
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followers = new HashSet<>();


    public User(String loginId, String username, String nickname, String password) { // 👈 닉네임 추가
        this.loginId = loginId;
        this.username = username;
        this.nickname = nickname; // 👈 닉네임 초기화
        this.password = password;
    }

}
