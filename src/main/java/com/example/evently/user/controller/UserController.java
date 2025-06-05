package com.example.evently.user.controller;

import com.example.evently.auth.CustomUserDetails;
import com.example.evently.global.dto.SuccessResponseDto;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 마이페이지 - 내 정보 조회
     * @param userDetails
     * @return
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponseDto user = userService.findByUserId(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    /**
     * 마이페이지 - 내 정보 수정
     * @param userDetails
     * @param userRequestDto
     * @return
     */
    @PutMapping
    public ResponseEntity<UserResponseDto> updateUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDetails.getUsername(), userRequestDto));
    }

    /**
     * 마이페이지 - 회원 탈퇴
     * @param userDetails
     * @return
     */
    @DeleteMapping
    public ResponseEntity<SuccessResponseDto> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            userService.softDeleteUser(userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(SuccessResponseDto.of("회원 탈퇴가 완료되었습니다."));
        }catch (IllegalStateException e){
            return ResponseEntity.badRequest().body(SuccessResponseDto.of(e.getMessage()));
        }
    }



}
