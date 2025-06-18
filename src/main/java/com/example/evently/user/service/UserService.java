package com.example.evently.user.service;

import com.example.evently.user.domain.User;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * 사용자 정보 조회
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public UserResponseDto findByUserId(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(()-> new IllegalArgumentException("정보를 찾을 수 없습니다 : " + userId));
        return UserResponseDto.fromEntity(user);
    }


    /**
     * 사용자 정보 수정
     * @param userId
     * @param requestDto
     * @return
     */
    @Transactional
    public UserResponseDto updateUser(String userId, UserRequestDto requestDto) {
        User user = userRepository.findByUserId(userId).orElseThrow(()-> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다 : " + userId));
        user.updateUser(
                requestDto.userName(),
                requestDto.password()
        );
        return UserResponseDto.fromEntity(user);
    }

    /**
     * 사용자 탈퇴
     * @param userId
     */
    @Transactional
    public void softDeleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(()->new IllegalArgumentException("회원정보를 찾을 수 없습니다. :" + userId));
        user.softDelete();
    }

    /**
     * 전체 사용자 리스트 조회
     * @return
     */
    public Page<UserResponseDto> findAllUsersWithFilters(
            String userId,
            String userName,
            String userRole,
            String isUse,
            String userStatus,
            Pageable pageable
    ) {
        return userRepository.findAllByAdminFilters(userId, userName, userRole, isUse, userStatus, pageable);
    }
}
