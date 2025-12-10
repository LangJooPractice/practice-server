package com.langjoo.prac.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    // 📌 수정 가능한 항목

    // 1. 유저네임 (고유성 검증이 필요)
    @Size(min = 4, max = 20, message = "유저네임은 4자에서 20자 사이여야 합니다.")
    private String username;

    // 2. 닉네임
    @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다.")
    private String nickname;

    // 3. 자기소개
    @Size(max = 200, message = "소개는 200자를 초과할 수 없습니다.")
    private String bio;

    // 4. 주소
    private String address;
}