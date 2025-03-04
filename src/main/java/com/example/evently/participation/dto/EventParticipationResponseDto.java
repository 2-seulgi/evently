package com.example.evently.participation.dto;

import java.time.LocalDateTime;

public record EventParticipationResponseDto(Long eventId, String eventName, LocalDateTime regDate) {
}
