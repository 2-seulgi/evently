package com.example.evently.event.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum RewardType {
    FIRST_COME_FIRST_SERVED ("선착순"),
    DRAW("랜덤"),
    INSTANT_WIN ("즉시 당첨");
    private final String description;

    @JsonCreator
    public static com.example.evently.event.domain.enums.RewardType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // 빈 문자열을 null로 변환
        }
        try {
            return com.example.evently.event.domain.enums.RewardType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 보상 유형입니다: " + value);
        }
    }
}
