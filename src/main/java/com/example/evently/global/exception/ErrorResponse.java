package com.example.evently.global.exception;


public record ErrorResponse(
        String message,
        String field,
        String code
) {
    // ✅ 기본 생성자 (메시지만 포함)
    public ErrorResponse(String message) {
        this(message, null, null);
    }

    // ✅ ExceptionType을 기반으로 생성하는 정적 메서드
    public static ErrorResponse of(ExceptionType exceptionType) {
        return new ErrorResponse(exceptionType.getMessage(), null, exceptionType.name());
    }

    // ✅ ExceptionType + 필드명을 포함하여 생성하는 정적 메서드
    public static ErrorResponse of(ExceptionType exceptionType, String field) {
        return new ErrorResponse(exceptionType.getMessage(), field, exceptionType.name());
    }
}
