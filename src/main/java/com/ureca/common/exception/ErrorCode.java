package com.ureca.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ACCOUNT_DATA_ERROR(400, "양식에 맞는 값을 입력해주세요.", 1000),
    HISTORY_NOT_EXIST(500, "조건에 맞는 history 정보가 없습니다.", 1100),
    // 프로필 관련 에러
    CUSTOMER_NOT_EXIST(500, "보호자 정보를 찾을 수 없습니다.", 2000),
    // 리뷰 관련 에러
    REVIEW_NOT_EXIST(500, "리뷰를 찾을 수 없습니다.", 3000),
    USER_CONFLICT_ERROR(409, "다른 사용자와 동시에 처리 중입니다.", 3100);

    private final int status; // HTTP 상태 코드
    private final String message; // 에러 메시지
    private final int code; // 애플리케이션 고유 에러 코드
}
