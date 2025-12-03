package com.langjoo.prac.user.repository;

import com.langjoo.prac.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // loginId를 사용하여 User를 찾는 메서드 (로그인 및 중복 확인 시 사용)
    Optional<User> findByLoginId(String loginId);

    // name(사용자 이름)을 사용하여 User를 찾는 메서드
    Optional<User> findByUsername(String username);

    // loginId 중복 여부를 빠르게 확인하는 메서드
    boolean existsByLoginId(String loginId);

    // username 중복 여부를 확인하는 메서드
    boolean existsByUsername(String username);
}
