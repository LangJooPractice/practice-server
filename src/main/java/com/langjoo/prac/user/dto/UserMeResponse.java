package com.langjoo.prac.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "현재 로그인 유저 정보 응답")
public class UserMeResponse {

    @Schema(description = "유저 고유 ID", example = "1")
    private Long userId;

    @Schema(description = "유저네임 (아이디)", example = "langjoo_dev")
    private String username;

    @Schema(description = "닉네임 (표시 이름)", example = "랑주")
    private String nickname;
}