package com.example.evently.global.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private final ExceptionType exceptionType;


    public GlobalException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
