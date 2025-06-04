package com.example.evently.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE,
    WITHDRAWN,
    SUSPENDED
}
