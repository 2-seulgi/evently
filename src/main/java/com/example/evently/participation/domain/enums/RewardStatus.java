package com.example.evently.participation.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RewardStatus {
    WIN("당첨"),     // 당첨
    LOSE("미당첨"),    // 미당첨
    PENDING("결과 미확정"); // 아직 결과 미확정 (추첨 대기 중 등)

    private final String description;

    @JsonCreator
    public static RewardStatus from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return RewardStatus.valueOf(value);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(value + " is not a valid reward status");
        }
    }
}
