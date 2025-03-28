package com.example.evently.auth.controller;

import com.example.evently.auth.dto.LoginRequestDto;
import com.example.evently.auth.service.AuthService;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.repository.UserRepository;
import com.example.evently.user.service.UserService;
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
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;


    /**
     * 로그인 api
     * @param requestDto
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto.userId(), requestDto.password()));
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

    /**
     * 중복 체크
     * @param email
     * @return
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailDuplicate(@RequestParam String email) {
        boolean exists = userRepository.existsByUserId(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("duplicate", exists);
        return ResponseEntity.ok(result);
    }

}
