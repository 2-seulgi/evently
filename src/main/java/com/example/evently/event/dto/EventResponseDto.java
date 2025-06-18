package com.example.evently.event.dto;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
@Schema(description = "이벤트 응답 DTO")
public record EventResponseDto(
        @Schema(description = "이벤트 ID", example = "1")
        Long id,

        @Schema(description = "이벤트 제목", example = "출석체크 이벤트")
        String title,

        @Schema(description = "이벤트 설명", example = "매일 출석 시 포인트 지급 이벤트입니다.")
        String description,

        @Schema(description = "이벤트 시작일", example = "2025-07-01T00:00:00")
        LocalDateTime startDate,

        @Schema(description = "이벤트 종료일", example = "2025-07-31T23:59:59")
        LocalDateTime endDate,

        @Schema(description = "이벤트 참여 시 지급 포인트", example = "100")
        int pointReward,

        @Schema(description = "삭제 여부", example = "false")
        boolean isDeleted,

        @Schema(description = "등록일", example = "2025-06-01T12:00:00")
        LocalDateTime regDate,

        @Schema(description = "수정일", example = "2025-06-10T14:30:00")
        LocalDateTime chgDate,

        @Schema(description = "이벤트 유형", example = "QUIZ")
        EventType eventType
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