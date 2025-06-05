package com.example.evently.user.dto;

import com.example.evently.user.domain.User;
import com.example.evently.user.domain.enums.UserRole;
import com.example.evently.user.domain.enums.UserStatus;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String userId,
        String userName,
        int points,
        boolean isUse,
        UserRole userRole,
        UserStatus userStatus,
        LocalDateTime regDate,
        LocalDateTime chgDate
) {
    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUserId(),
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
