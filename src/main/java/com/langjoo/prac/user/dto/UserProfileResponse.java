package com.langjoo.prac.user.dto;

import com.langjoo.prac.domain.User;
import com.langjoo.prac.tweet.dto.TweetResponse; // 해당 유저의 트윗 목록을 담기 위해 사용
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserProfileResponse {

    private Long userId;
    private String username;    // @아이디
    private String nickname;    // 표시 이름
    private LocalDateTime joinedAt; // 가입 일시

    // 통계 정보
    private long tweetCount;       // 작성한 총 트윗 수 (리트윗 포함)
    private long followingCount;   // 팔로잉 수
    private long followerCount;    // 팔로워 수
    private boolean isFollowing;    // 현재 로그인 유저가 이 유저를 팔로우하고 있는지 여부 (매우 중요)

    // 해당 유저가 작성한 최신 트윗 목록 (개인 타임라인)
    private List<TweetResponse> recentTweets;

    // 📌 엔티티 및 데이터를 DTO로 변환하는 팩토리 메서드
    public static UserProfileResponse from(
            User user,
            long tweetCount,
            long followingCount,
            long followerCount,
            boolean isFollowing,
            List<TweetResponse> recentTweets) {

        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .joinedAt(user.getCreatedAt()) // BaseEntity에서 상속받은 createdAt

                .tweetCount(tweetCount)
                .followingCount(followingCount)
                .followerCount(followerCount)
                .isFollowing(isFollowing)

                .recentTweets(recentTweets)
                .build();
    }
}