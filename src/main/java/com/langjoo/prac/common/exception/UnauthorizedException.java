package com.langjoo.prac.common.exception;

public class UnauthorizedException extends RuntimeException {

    // 1. 기본 생성자
    public UnauthorizedException() {
        super("해당 작업을 수행할 권한이 없습니다.");
    }

    // 2. 메시지를 받는 생성자 (가장 흔하게 사용)
    public UnauthorizedException(String message) {
        super(message);
    }

    // 3. 메시지와 원인 예외를 받는 생성자
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}