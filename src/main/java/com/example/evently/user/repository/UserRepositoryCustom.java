package com.example.evently.user.repository;

import com.example.evently.user.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<UserResponseDto> findAllByAdminFilters(String userId, String userName, String userRole, String isUse, String userStatus, Pageable pageable);
}
