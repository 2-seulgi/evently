package com.example.evently.user.controller;

import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

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

}
