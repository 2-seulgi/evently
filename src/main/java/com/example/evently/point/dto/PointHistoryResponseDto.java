package com.example.evently.point.dto;

import com.example.evently.point.domain.PointHistory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 포인트 내역 응답 DTO
 * @param points
 * @param reason
 * @param createdAt
 */
@Schema(description = "포인트 내역 응답 DTO")
public record PointHistoryResponseDto(
        @Schema(description = "포인트 증감 값 (양수: 적립, 음수: 차감)", example = "100")
        int points,

        @Schema(description = "포인트 적립/차감 사유", example = "이벤트 참여 보상")
        String reason,

        @Schema(description = "발생 일시", example = "2025-06-10T13:15:00")
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
