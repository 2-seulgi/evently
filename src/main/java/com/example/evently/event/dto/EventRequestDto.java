package com.example.evently.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record EventRequestDto (
        @NotBlank(message = "이벤트 제목은 필수입니다.") String title,
        String description,
        @NotNull(message = "이벤트 시작 날짜는 필수입니다.") @Future(message = "시작 날짜는 현재보다 미래여야 합니다.")
        LocalDateTime startDate,
        @NotNull(message = "이벤트 종료 날짜는 필수입니다.") @Future(message = "종료 날짜는 현재보다 미래여야 합니다.") LocalDateTime endDate,
        @Min(value = 0, message = "포인트는 0 이상이어야 합니다.") int pointReward
){
        public EventRequestDto {
                if (title == null || title.isBlank()) {
                        throw new IllegalArgumentException("이벤트 제목은 필수 입력 사항입니다.");
                }
                if (startDate == null || endDate == null) {
                        throw new IllegalArgumentException("이벤트 시작일과 종료일은 필수입니다.");
                }
                if (endDate.isBefore(startDate)) {
                        throw new IllegalArgumentException("종료일은 시작일보다 이후여야 합니다.");
                }
                if (pointReward < 0) {
                        throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
                }
        }
}
