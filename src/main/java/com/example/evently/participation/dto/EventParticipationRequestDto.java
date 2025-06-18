package com.example.evently.participation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이벤트 참가 요청 DTO")
public record EventParticipationRequestDto(
        @Schema(description = "참여 사용자 식별자(PK)", example = "1")
        Long userSn, // 참여할 사용자 식별자
        @Schema(description = "참여한 이벤트 식별자(PK)", example = "1")
        Long eventSn // 참여할 이벤트 식별자
) {
}
