package com.example.evently.user.dto;

import com.example.evently.user.domain.User;
import com.example.evently.user.domain.enums.UserRole;
import com.example.evently.user.domain.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "사용자 응답 DTO")
public record UserResponseDto(
        @Schema(description = "사용자 식별자(PK)")
        Long id,

        @Schema(description = "사용자 ID" , example = "test@test.com")
        String userId,

        @Schema(description = "사용자 이름" , example = "테스터")
        String userName,

        @Schema(description = "사용자 포인트" , example = "3")
        int points,

        @Schema(description = "사용자 사용 여부" , example = "Y")
        boolean isUse,

        @Schema(description = "사용자 롤" , example = "USER")
        UserRole userRole,

        @Schema(description = "사용자 상태" , example = "ACTIVE")
        UserStatus userStatus,

        @Schema(description = "사용자 등록일", example = "2025-07-01T09:00:00")
        LocalDateTime regDate,

        @Schema(description = "사용자 수정일", example = "2025-07-01T09:00:00")
        LocalDateTime chgDate
) {
    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getLoginId(),
                user.getUserName(),
                user.getPoints(),
                user.isUse(),
                user.getUserRole(),
                user.getUserStatus(),
                user.getRegDate(),
                user.getChgDate()
        );
    }


}
