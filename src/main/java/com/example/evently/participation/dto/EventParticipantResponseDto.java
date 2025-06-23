package com.example.evently.participation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 관리자용 이벤트별 참여자 조회 (유저를 기준으로 참여 정보 제공)
 * @param userId
 * @param userName
 * @param regDate
 */
@Schema(description = "이벤트 참여자 응답 DTO (관리자용)")
public record EventParticipantResponseDto(
        @Schema(description = "사용자 식별자(PK)", example = "101")
        Long userId,

        @Schema(description = "사용자 이름", example = "홍길동")
        String userName,

        @Schema(description = "이벤트 참여 일시", example = "2025-07-01T09:00:00")
        LocalDateTime regDate
) {
}
