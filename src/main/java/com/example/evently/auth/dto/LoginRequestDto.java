package com.example.evently.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @Schema(description = "사용자 ID", example = "user@example.com")
        @NotBlank(message = "아이디를 입력해주세요.") String loginId,
        @Schema(description = "비밀번호", example = "1234asdf")
        @NotBlank(message = "비밀번호를 입력해주세요.") String password
) {
}
