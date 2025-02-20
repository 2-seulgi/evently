package com.example.evently.event.dto;

import com.example.evently.event.domain.Event;

import java.time.LocalDateTime;

public record EventResponseDto(
        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int pointReward,
        boolean isDeleted
) {
    public static EventResponseDto fromEntity(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getPointReward(),
                event.isDeleted()
        );
    }
}