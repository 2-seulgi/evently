package com.example.evently.participation.dto;

import java.time.LocalDateTime;

/**
 * 관리자용 이벤트별 참여자 조회 (유저를 기준으로 참여 정보 제공)
 * @param userId
 * @param userName
 * @param regDate
 */
public record EventParticipantResponseDto(
        Long userId,
        String userName,
        LocalDateTime regDate
) {
}
