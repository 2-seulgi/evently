package com.example.evently.event.dto;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;

import java.time.LocalDateTime;

public record EventResponseDto(
        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int pointReward,
        boolean isDeleted,
        LocalDateTime regDate,
        LocalDateTime chgDate,
        EventType eventType // 이벤트 유형 추가

) {

    public static EventResponseDto fromEntity(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getPointReward(),
                event.isDeleted(),
                event.getRegDate(),
                event.getChgDate(),
                event.getEventType()
        );
    }
}