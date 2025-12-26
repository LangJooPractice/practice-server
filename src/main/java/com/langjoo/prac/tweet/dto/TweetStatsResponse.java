package com.langjoo.prac.tweet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "트윗 통계 정보 응답")
public class TweetStatsResponse {

    @Schema(description = "트윗 ID", example = "1")
    private Long tweetId;

    @Schema(description = "좋아요 개수", example = "15")
    private int likeCount;

    @Schema(description = "리트윗 개수", example = "8")
    private int retweetCount;

    @Schema(description = "답글 개수", example = "4")
    private int replyCount;
}