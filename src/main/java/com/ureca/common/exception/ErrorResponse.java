package com.ureca.common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;
    private final int status;
    private final int code;

    @Builder
    public ErrorResponse(String message, int status, int code) {
        this.message = message;
        this.status = status;
        this.code = code;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .message(errorCode.getMessage())
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .build();
    }
}
