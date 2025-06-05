package com.example.evently.user.controller;

import com.example.evently.user.domain.User;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    /**
     * 관리자 유저 리스트
     * @param userId
     * @param userName
     * @param userRole
     * @param isUse
     * @param userStatus
     * @param page
     * @param size
     * @param sortBy
     * @param direction
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(required = false)String userId,
            @RequestParam(required = false)String userName,
            @RequestParam(required = false)String userRole,
            @RequestParam(required = false)String isUse,
            @RequestParam(required = false)String userStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "regDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ){
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc") ?
                        Sort.by(sortBy).ascending() :
                        Sort.by(sortBy).descending());
        Page<UserResponseDto> users = userService.findAllUsersWithFilters(userId, userName, userRole, isUse, userStatus, pageable);

        return ResponseEntity.ok(users);
    }

    /**
     * 관리자 유저 상세
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findByUserId(userId));
    }

    /**
     * 관리자 유저 수정
     * @param userId
     * @param userRequestDto
     * @return
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String userId, @Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.updateUser(userId, userRequestDto));
    }

    /**
     * 관리자 회원 탈퇴 처리
     * @param userId
     * @return
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.softDeleteUser(userId);
        return ResponseEntity.noContent().build();
    }


}
