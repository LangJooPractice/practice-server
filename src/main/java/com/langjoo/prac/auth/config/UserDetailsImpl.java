package com.langjoo.prac.auth.config;

import com.langjoo.prac.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// Spring Security의 UserDetails 인터페이스를 구현하는 클래스
public class UserDetailsImpl implements UserDetails {

    // 📌 DB 기본키 (Long id)를 userId로 저장하여 Service 계층에서 사용
    private final Long userId;

    // 로그인 ID (loginId)를 username으로 사용
    private final String loginId;
    private final String password;

    // User 엔티티를 받아 UserDetailsImpl 객체를 생성하는 생성자
    public UserDetailsImpl(User user) {
        this.userId = user.getId();
        this.loginId = user.getLoginId();
        this.password = user.getPassword();
    }

    // 📌 Service 계층에서 사용할 userId Getter
    public Long getUserId() {
        return userId;
    }

    // --- UserDetails 인터페이스 구현 메서드 ---

    // 사용자의 권한 목록을 반환 (트위터 클론 코딩에서는 복잡한 권한이 없을 경우 단순하게 처리)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 관리가 필요하다면 여기에 로직 추가
        return Collections.emptyList();
    }

    // 비밀번호 반환
    @Override
    public String getPassword() {
        return this.password;
    }

    // 사용자 이름(여기서는 loginId) 반환
    @Override
    public String getUsername() {
        return this.loginId;
    }

    // 계정 만료 여부 (true = 만료 안됨)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부 (true = 잠금 안됨)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부 (true = 만료 안됨)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부 (true = 활성화)
    @Override
    public boolean isEnabled() {
        return true;
    }
}