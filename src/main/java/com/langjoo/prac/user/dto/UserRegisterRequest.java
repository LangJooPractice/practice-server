package com.langjoo.prac.user.dto;

import com.langjoo.prac.common.GlobalConstants; // 트윗 길이 제한 상수를 정의했던 곳에 사용자 관련 상수도 추가 가능
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRegisterRequest {

    // 1. 로그인 ID (loginId)
    // 일반적으로 이메일 형식을 사용하지만, 여기서는 사용자 정의 Login ID로 가정
    @NotBlank(message = "로그인 ID는 필수입니다.")
    @Size(min = 4, max = 20, message = "로그인 ID는 4자 이상 20자 이하로 설정해야 합니다.")
    private String loginId;

    // 2. 고유 Username (@아이디)
    @NotBlank(message = "사용자 이름(@아이디)은 필수입니다.")
    @Size(min = 3, max = 15, message = "사용자 이름은 3자 이상 15자 이하로 설정해야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자 이름은 영문, 숫자, 언더바(_)만 사용 가능합니다.")
    private String username;

    // 3. 닉네임 (표시 이름)
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 50, message = "닉네임은 50자 이하로 설정해야 합니다.")
    private String nickname;

    // 4. 비밀번호
    @NotBlank(message = "비밀번호는 필수입니다.")
    // 보안을 위해 비밀번호 정책을 강제하는 정규식 패턴을 추가할 수 있습니다.
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
}