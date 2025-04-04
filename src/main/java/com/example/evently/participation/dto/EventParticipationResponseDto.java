package com.example.evently.participation.dto;

import java.time.LocalDateTime;

/**
 * 사용자용 참여 내역 조회(사용자가 참여한 이벤트 정보 제공)
 * @param eventId
 * @param eventName
 * @param regDate
 */
public record EventParticipationResponseDto(Long eventId, String eventName, LocalDateTime regDate) {
}
