package com.langjoo.prac.common;

// 이 클래스는 인스턴스화 될 필요가 없으므로 final로 선언하고 private 생성자를 가집니다.
public final class GlobalConstants {

    // private 생성자로 인스턴스화를 방지
    private GlobalConstants() {
        throw new IllegalStateException("Utility class");
    }

    // 📌 트윗 길이 제한 상수
    public static final int TWEET_MAX_LENGTH = 280;

    // (만약 필요하다면 다른 전역 상수들을 여기에 추가할 수 있습니다.)
    // public static final String DEFAULT_PROFILE_IMAGE_URL = "...";
}
