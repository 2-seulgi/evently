package com.example.evently.participation.dto;

public record EventParticipationRequestDto(
        Long userSn, // 참여할 사용자 ID
        Long eventId // 참여할 이벤트 ID
) {
}
