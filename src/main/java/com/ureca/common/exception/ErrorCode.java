package com.ureca.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ACCOUNT_DATA_ERROR(400, "양식에 맞는 값을 입력해주세요.", 1000),
    HISTORY_NOT_EXIST(500, "조건에 맞는 Reservation history 정보가 없습니다.", 1100),
    DATA_NOT_EXIST(500, "조건에 맞는 데이터 정보가 없습니다.", 1200);

    private final int status; // HTTP 상태 코드
    private final String message; // 에러 메시지
    private final int code; // 애플리케이션 고유 에러 코드
}
