package com.langjoo.prac.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "북마크 토글 응답")
public class BookmarkToggleResponse {
    @Schema(description = "트윗 ID", example = "1")
    private Long tweetId;

    @Schema(description = "북마크 여부 (최종 상태)", example = "true")
    private boolean isBookmarked;

    // 선택사항: 현재 트윗의 총 좋아요 수도 같이 보내주면 프론트가 새로고침 없이 숫자를 갱신하기 좋습니다.
    @Schema(description = "트윗의 현재 총 북마크 수", example = "10")
    private int bookmarkCount;

}