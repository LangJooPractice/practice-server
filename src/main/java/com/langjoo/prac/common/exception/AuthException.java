package com.langjoo.prac.common.exception;

// RuntimeException을 상속받아 Unchecked Exception으로 만듭니다.
// 이는 트랜잭션 롤백에 용이하고, 명시적인 throws 선언 없이 사용할 수 있어 편리합니다.
public class AuthException extends RuntimeException {

    // 1. 기본 생성자
    public AuthException() {
        super("인증 관련 처리 중 오류가 발생했습니다.");
    }

    // 2. 메시지를 받는 생성자 (가장 흔하게 사용)
    public AuthException(String message) {
        super(message);
    }

    // 3. 메시지와 원인 예외를 받는 생성자
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}