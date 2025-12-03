package com.langjoo.prac.auth.config;// UserDetailsServiceImpl.java (auth.config 패키지에 함께 정의)

import com.langjoo.prac.domain.User;
import com.langjoo.prac.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security가 인증 과정에서 사용자 이름을 받아 이 메서드를 호출
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // loginId를 사용하여 DB에서 User 엔티티를 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId));

        // 조회된 User 엔티티를 기반으로 UserDetailsImpl 객체를 생성하여 반환
        return new UserDetailsImpl(user);
    }
}