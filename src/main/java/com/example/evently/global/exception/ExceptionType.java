package com.example.evently.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다."),
    EVENT_CANNOT_BE_DELETED(HttpStatus.BAD_REQUEST, "이미 종료된 이벤트는 삭제할 수 없습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "종료일은 시작일보다 이후여야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
