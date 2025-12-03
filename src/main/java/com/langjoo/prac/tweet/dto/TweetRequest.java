package com.langjoo.prac.tweet.dto; // 📌 Tweet 관련 DTO 패키지에 위치

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.langjoo.prac.common.GlobalConstants.TWEET_MAX_LENGTH;

@Getter
@NoArgsConstructor // JSON 역직렬화를 위한 기본 생성자
public class TweetRequest {

    // 트윗 본문 (필수)
    @NotBlank(message = "트윗 내용은 빈 값이거나 공백일 수 없습니다.") // Null, Empty, Blank 모두 허용하지 않음
    @Size(max = TWEET_MAX_LENGTH, message = "트윗 내용은 최대 "+TWEET_MAX_LENGTH+"자까지만 허용됩니다.") // 트위터의 280자 제한
    private String content;

    // 💡 참고: 일반 트윗 작성 시에는 이 필드만 사용합니다.
    // 💡 인용 리트윗 시에도 이 필드를 사용하여 인용 내용을 전달합니다.
}