package com.example.evently.auth.controller;

import com.example.evently.auth.dto.LoginRequestDto;
import com.example.evently.auth.service.AuthService;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입 및 로그인 인증 관련 API 입니다.")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;


    /**
     * 로그인 api
     * @param requestDto
     * @return
     */
    @Operation(summary = "로그인", description = "아이디 및 비밀번호를 확인하고 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto.loginId(), requestDto.password()));
    }

    /**
     * 회원 가입
     * @param userRequestDto
     * @return
     */
    @Operation(summary = "회원가입", description = "중복 아이디를 확인하고 비밀번호를 암호화 한 후 회원 정보를 저장합니다.")
    @PostMapping
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto responseDto = authService.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 중복 체크
     * @param loginId
     * @return
     */
    @Operation(summary = "아이디 중복 체크", description = "중복 아이디를 체크 합니다.")
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkIdDuplicate(
            @Parameter(description = "사용자 아이디", example = "userId@example.com")
            @RequestParam String loginId) {
        boolean exists = userRepository.existsByLoginId(loginId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("duplicate", exists);
        return ResponseEntity.ok(result);
    }

}
