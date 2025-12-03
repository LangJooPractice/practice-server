package com.langjoo.prac.common.exception;


public class DuplicateException extends RuntimeException {

    // 1. 기본 생성자
    public DuplicateException() {
        super("이미 존재하는 리소스입니다.");
    }

    // 2. 메시지를 받는 생성자 (가장 흔하게 사용)
    public DuplicateException(String message) {
        super(message);
    }

    // 3. 메시지와 원인 예외를 받는 생성자
    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}