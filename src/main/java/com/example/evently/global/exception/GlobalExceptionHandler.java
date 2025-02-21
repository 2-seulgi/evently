package com.example.evently.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 비즈니스 로직에서 발생하는 예외 (최소한의 공통 예외 처리)
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(GlobalException ex) {
        return ResponseEntity
                .status(ex.getExceptionType().getHttpStatus())
                .body(ErrorResponse.of(ex.getExceptionType()));
    }

    // ✅ DTO 유효성 검증 실패 예외 처리 (@Valid 체크)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage(); // 첫 번째 오류 메시지 반환
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage));
    }

    // ✅ IllegalArgumentException 처리 (DTO 내부에서 예외 발생 시)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage())); // 예외 메시지 그대로 반환
    }

    // ✅ 기타 예상하지 못한 예외 처리 (디버깅을 위해 추가)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버 내부 오류가 발생했습니다."));
    }
}

