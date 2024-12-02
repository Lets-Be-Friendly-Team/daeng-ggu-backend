package com.ureca.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 1000번대: 입력 데이터 관련 오류
    ACCOUNT_DATA_ERROR(400, "양식에 맞는 값을 입력해주세요.", 1000),
    DATA_VALIDATION_ERROR(400, "입력된 데이터가 유효하지 않습니다.", 1010),

    // 2000번대: 데이터 조회 관련 오류
    DATA_NOT_EXIST(500, "조건에 맞는 데이터 정보가 없습니다.", 2000),
    USER_NOT_EXIST(500, "해당하는 유저가 없습니다.", 2010),
    REVIEW_NOT_EXIST(500, "리뷰를 찾을 수 없습니다.", 2020),
    CUSTOMER_NOT_EXIST(500, "보호자 정보를 찾을 수 없습니다.", 2030),
    DESIGNER_NOT_EXIST(500, "미용사 정보를 찾을 수 없습니다.", 2040),

    // 3000번대: 인증 및 권한 관련 오류
    ACCESS_DENIED(403, "데이터 접근 권한이 없습니다.", 3000),

    // 4000번대: 예약 및 비즈니스 로직 관련 오류
    HISTORY_NOT_EXIST(500, "조건에 맞는 Reservation history 정보가 없습니다.", 4000),
    USER_CONFLICT_ERROR(409, "다른 사용자와 동시에 처리 중입니다.", 4010),

    // 5000번대 : 파일처리 관련 오류
    FILE_NOT_EXIST(500, "파일 업로드에 실패했습니다.", 5100);

    private final int status; // HTTP 상태 코드
    private final String message; // 에러 메시지
    private final int code; // 애플리케이션 고유 에러 코드
}
