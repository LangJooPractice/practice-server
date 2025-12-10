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

    // Spring Security가 인증 과정에서 사용자 ID(String)를 받아 이 메서드를 호출
    @Override
    public UserDetails loadUserByUsername(String userIdString) throws UsernameNotFoundException {
        // 1. JWT에서 넘어온 String 타입의 ID를 Long 타입으로 변환
        Long userId = Long.parseLong(userIdString);

        // 2. 📌 UserRepository의 findById(Long id)를 사용하여 User 엔티티 조회
        User user = userRepository.findById(userId) // 👈 findByLoginId 대신 findById 사용
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userIdString));

        // 조회된 User 엔티티를 기반으로 UserDetailsImpl 객체를 생성하여 반환
        return new UserDetailsImpl(user);
    }
}