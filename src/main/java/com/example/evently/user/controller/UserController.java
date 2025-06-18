package com.example.evently.user.controller;

import com.example.evently.auth.CustomUserDetails;
import com.example.evently.global.dto.SuccessResponseDto;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(
        name = "마이페이지 - 사용자 정보 관리 API",
        description = "사용자는 본인의 정보를 조회, 수정하거나 회원 탈퇴를 수행할 수 있습니다."
)
public class UserController {
    private final UserService userService;

    /**
     * 마이페이지 - 내 정보 조회
     * @param userDetails
     * @return
     */
    @Operation(summary = "마이페이지 - 본인 정보 조회 API" , description = "로그인한 사용자의 계정 정보를 조회합니다.")
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
    @Operation(summary = "마이페이지 - 본인 정보 수정 API" ,  description = "로그인한 사용자의 계정을 소프트 삭제(soft delete) 방식으로 탈퇴 처리합니다.")
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
    @Operation(summary = "마이페이지 - 회원 탈퇴 API" , description = "로그인한 사람의 아이디를 기반으로 회원 탈퇴 처리를 한다(soft delete 사용). " )
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
