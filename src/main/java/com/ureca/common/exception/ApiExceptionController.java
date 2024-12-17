package com.ureca.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// import io.jsonwebtoken.ExpiredJwtException;
// import io.jsonwebtoken.JwtException;

@RestControllerAdvice(basePackages = "com.ureca")
public class ApiExceptionController {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .code(exception.getErrorCode().getCode())
                        .status(exception.getErrorCode().getStatus())
                        .message(exception.getErrorCode().getMessage())
                        .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON) // Content-Type 명시
                .body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 500, 500);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON) // Content-Type 명시
                .body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 500, 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON) // Content-Type 명시
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        ErrorResponse errorResponse =
                new ErrorResponse(
                        exception.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                        400,
                        400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON) // Content-Type 명시
                .body(errorResponse);
    }

    // @ExceptionHandler(JwtException.class)
    // public ResponseEntity<ErrorResponse> handleJwtException(JwtException exception) {
    // 	ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 401, 401);
    // 	return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    // }
    //
    // @ExceptionHandler(AccessDeniedException.class)
    // public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException
    // exception) {
    // 	ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), 500, 403);
    // 	return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    // }
    //
    // @ExceptionHandler(ExpiredJwtException.class)
    // public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException exception)
    // {
    // 	ErrorResponse errorResponse = new ErrorResponse("JWT 토큰이 만료되었습니다. RefreshToken으로 재로그인해주세요",
    // 401, 401);
    // 	return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    // }
}
