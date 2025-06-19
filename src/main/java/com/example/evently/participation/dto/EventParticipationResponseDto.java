package com.example.evently.participation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 사용자용 참여 내역 조회(사용자가 참여한 이벤트 정보 제공)
 * @param eventSn
 * @param eventTitle
 * @param regDate
 */
@Schema(description = "이벤트 참여 응답 DTO" )
public record EventParticipationResponseDto(
        @Schema(description = "이벤트 식별자(PK)", example = "1")
        Long eventSn,
        @Schema(description = "이벤트 제목", example = "출석체크 이벤트")
        String eventTitle,
        @Schema(description = "이벤트 참여 일시", example = "2025-07-01T09:00:00")
        LocalDateTime regDate) {

}
