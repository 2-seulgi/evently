package com.example.evently.event.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    SURVEY("설문조사"),
    QUIZ("퀴즈"),
    PROMOTION("프로모션"),
    GIVEAWAY("이벤트 경품"),
    CHECKIN("출석체크"),
    OTHER("기타");

    private final String description;

    @JsonCreator
    public static EventType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // 빈 문자열을 null로 변환
        }
        try {
            return EventType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 이벤트 유형입니다: " + value);
        }
    }
}
