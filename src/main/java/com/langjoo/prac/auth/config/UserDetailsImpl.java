package com.langjoo.prac.auth.config;

import com.langjoo.prac.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserDetailsImpl implements UserDetails {

    // 📌 [수정] 엔티티 자체를 필드로 가집니다.
    private final User user;

    // User 엔티티를 받아 필드에 할당합니다.
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // --- UserDetails 인터페이스 구현 메서드 ---

    @Override
    public String getPassword() {
        // 📌 엔티티에서 직접 가져옵니다.
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // 📌 엔티티에서 직접 가져옵니다. (loginId가 username 역할)
        return user.getLoginId();
    }

    // 💡 편의를 위해 userId를 반환하는 메서드를 유지하거나 추가할 수 있습니다.
    public Long getUserId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}