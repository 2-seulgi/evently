package com.example.evently.point.dto;

import com.example.evently.point.domain.PointHistory;

import java.time.LocalDateTime;

public record PointHistoryResponseDto(
        int points,
        String reason,
        LocalDateTime createdAt
) {
    public static PointHistoryResponseDto fromEntity(PointHistory pointHistory) {
        return new PointHistoryResponseDto(
            pointHistory.getPoints(),
            pointHistory.getReason(),
            pointHistory.getCreatedAt()
        );
    }

}
