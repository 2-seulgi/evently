package com.example.evently.event.dto;

import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.domain.enums.RewardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
@Schema(description = "이벤트 요청 DTO")
public record EventRequestDto (
        @Schema(description = "이벤트 제목", example = "출석체크 이벤트")
        @NotBlank(message = "이벤트 제목은 필수입니다.")
        String title,

        @Schema(description = "이벤트 설명", example = "2025 6월 매일 출석체크 이벤트 입니다. 꾸준히 참여해주세요.")
        String description,

        @Schema(description = "이벤트 시작일", example = "2025-05-18 07:24:48")
        @NotNull(message = "이벤트 시작 날짜는 필수입니다.") @Future(message = "시작 날짜는 현재보다 미래여야 합니다.")
        LocalDateTime startDate,

        @Schema(description = "이벤트 종료일", example = "2025-06-18 07:24:48")
        @NotNull(message = "이벤트 종료 날짜는 필수입니다.") @Future(message = "종료 날짜는 현재보다 미래여야 합니다.")
        LocalDateTime endDate,

        @Schema(description = "이벤트 보상 포인트", example = "2")
        @Min(value = 0, message = "포인트는 0 이상이어야 합니다.")
        int pointReward,

        @Schema(description = "이벤트 유형", example = "CHECKIN")
        @NotNull(message = "이벤트 유형은 필수입니다.")
        EventType eventType,

        @Schema(description = "보상 유형", example = "CHECKIN") RewardType rewardType
)
{

}
