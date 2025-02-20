package com.example.evently.global.dto;

public record SuccessResponseDto(String msg) {
    public static SuccessResponseDto of(String msg) {
        return new SuccessResponseDto(msg);
    }

}
