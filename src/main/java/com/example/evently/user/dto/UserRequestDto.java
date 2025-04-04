package com.example.evently.user.dto;

import com.example.evently.user.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank(message = "아이디는 필수입니다.") String userId,
        @NotBlank(message = "이름은 필수입니다.") String userName,
        @NotNull @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.") String password
) {

}
