package com.example.evently.user.service;

import com.example.evently.event.domain.Event;
import com.example.evently.event.repository.EventRepository;
import com.example.evently.user.domain.User;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * 사용자 정보 조회
     * @param username
     * @return
     */
    @Transactional(readOnly = true)
    public UserResponseDto findByUserId(String username) {
        User user = userRepository.findByUserId(username).orElseThrow(()-> new IllegalArgumentException("정보를 찾을 수 없습니다 : " + username));
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

    @Transactional
    public void softDeleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(()->new IllegalArgumentException("회원정보를 찾을 수 없습니다. :" + userId));
        user.softDelete();
    }
}
