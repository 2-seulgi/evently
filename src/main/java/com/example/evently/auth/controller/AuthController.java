package com.example.evently.auth.controller;

import com.example.evently.auth.dto.LoginRequestDto;
import com.example.evently.auth.service.AuthService;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 로그인 api
     * @param requestDto
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDto requestDto) {
        String token = authService.login(requestDto.userId(), requestDto.password());
        return ResponseEntity.ok(token);
    }

    /**
     * 회원 가입
     * @param userRequestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto responseDto = authService.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
