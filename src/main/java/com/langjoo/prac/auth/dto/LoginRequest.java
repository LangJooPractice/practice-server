package com.langjoo.prac.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JSON 역직렬화를 위한 기본 생성자
public class LoginRequest {

    // 1. 로그인 ID (loginId)
    // 사용자가 시스템에 인증할 때 사용하는 ID입니다.
    @NotBlank(message = "로그인 ID는 필수 입력 항목입니다.")
    @Size(min = 4, max = 20, message = "로그인 ID 형식이 올바르지 않습니다.") // 회원가입 시 설정한 최소/최대 길이와 일치하도록 설정
    private String loginId;

    // 2. 비밀번호 (password)
    // 암호화되어 저장된 비밀번호와 비교할 값입니다.
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.") // 보안을 위해 최소 길이 제약 조건을 다시 확인
    private String password;
}