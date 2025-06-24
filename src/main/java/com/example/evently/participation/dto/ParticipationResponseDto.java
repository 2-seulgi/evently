package com.example.evently.participation.dto;

import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.domain.enums.RewardType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이벤트 참여 응답 결과 DTO")
public record ParticipationResponseDto(
        @Schema(description = "포인트 지급/차감", example = "10")
        int pointReward,
        @Schema(description = "이벤트 타입", example = "GIVEAWAY")
        EventType eventType,
        @Schema(description = "보상 타입 (nullable)", example = "DRAW")
        RewardType rewardType
) {}