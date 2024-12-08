package com.ureca.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 1000번대: 입력 데이터 관련 오류
    ACCOUNT_DATA_ERROR(400, "양식에 맞는 값을 입력해주세요.", 1000),
    DATA_VALIDATION_ERROR(400, "입력된 데이터가 유효하지 않습니다.", 1010),
    DATA_NOT_AFTER_CURRENT(400, "입력된 데이터가 현재 이전 데이터입니다.", 1020),

    // 2000번대: 데이터 조회 관련 오류
    DATA_NOT_EXIST(500, "조건에 맞는 데이터 정보가 없습니다.", 2000),
    USER_NOT_EXIST(500, "해당하는 유저가 없습니다.", 2010),
    REVIEW_NOT_EXIST(500, "리뷰를 찾을 수 없습니다.", 2020),
    CUSTOMER_NOT_EXIST(500, "보호자 정보를 찾을 수 없습니다.", 2030),
    DESIGNER_NOT_EXIST(500, "디자이너 정보를 찾을 수 없습니다.", 2040),
    PET_NOT_EXIST(500, "반려견 정보를 찾을 수 없습니다.", 2050),
    REQUEST_NOT_EXIST(500, "요청서 정보를 찾을 수 없습니다.", 2060),
    ESTIMATE_NOT_EXIST(500, "견적서 정보를 찾을 수 없습니다.", 2070),
    INVALID_BREED(500, "잘못된 견종 코드입니다.", 2080),

    // 3000번대: 인증 및 권한 관련 오류
    ACCESS_DENIED(403, "데이터 접근 권한이 없습니다.", 3000),
    KAKAO_AUTHORIZE_DENIED(500, "카카오 인가 코드 획득에 실패했습니다.", 3010),

    // 4000번대: 예약 및 비즈니스 로직 관련 오류
    HISTORY_NOT_EXIST(500, "조건에 맞는 Reservation history 정보가 없습니다.", 4000),
    USER_CONFLICT_ERROR(409, "다른 사용자와 동시에 처리 중입니다.", 4010),
    SAME_USER_RESERVE_DENIED(400, "본인에게 예약은 불가능합니다.", 4020),
    RESERVE_EXIST_ERROR(400, "예약이 존재해 삭제할 수 없습니다.", 4030),

    // 5000번대 : 파일처리 관련 오류
    FILE_NOT_EXIST(500, "파일 업로드에 실패했습니다.", 5010),

    // 6000번대: 결제 관련 오류
    PAYMENT_SERVER_ERROR(500, "결제 서버 오류가 발생했습니다.", 6000),
    PAYMENT_VALIDATION_FAILED(400, "결제 요청 데이터가 유효하지 않습니다.", 6010),
    PAYMENT_PROCESS_FAILED(500, "결제 처리 중 오류가 발생했습니다.", 6020),

    // 7000 : 견적 관련 오류
    REQUEST_FULL_ESTIMATE(500, "모든 견적서가 전송되었습니다", 7000);

    private final int status; // HTTP 상태 코드
    private final String message; // 에러 메시지
    private final int code; // 애플리케이션 고유 에러 코드
}
