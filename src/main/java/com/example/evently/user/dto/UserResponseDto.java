package com.example.evently.user.dto;

import com.example.evently.user.domain.User;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String userId,
        String userName,
        int points,
        LocalDateTime regDate,
        LocalDateTime chgDate
) {
    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUserId(),
                user.getUserName(),
                user.getPoints(),
                user.getRegDate(),
                user.getChgDate()
        );
    }


}
