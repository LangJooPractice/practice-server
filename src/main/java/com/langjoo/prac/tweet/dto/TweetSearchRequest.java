package com.langjoo.prac.tweet.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate; // 📌 [수정] LocalDateTime 대신 LocalDate 사용

@Getter
@Setter
public class TweetSearchRequest {

    private String keyword;

    // 📌 [수정] 날짜 형식 변경: yyyy-MM-dd (시간 정보 제거)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate since;

    // 📌 [수정] 날짜 형식 변경: yyyy-MM-dd (시간 정보 제거)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate until;

    public boolean isValid() {
        return keyword != null && !keyword.trim().isEmpty() || since != null || until != null;
    }
}