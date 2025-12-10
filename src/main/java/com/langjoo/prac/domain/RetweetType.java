package com.langjoo.prac.domain;

public enum RetweetType {
    // 1. 일반적인 사용자가 작성한 트윗
    ORIGINAL,

    // 2. 코멘트 없이 공유만 하는 행위 (유일성 강제 대상)
    PURE_RETWEET,

    // 3. 코멘트를 추가하여 공유하는 행위 (새로운 트윗처럼 취급)
    QUOTE_RETWEET
}
