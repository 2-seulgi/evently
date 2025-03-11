package com.example.evently.user.service;

import com.example.evently.user.domain.User;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        // 1. 중복 검사
        if (userRepository.existsByUserId(userRequestDto.userId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 2. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(userRequestDto.password());

        User user = User.of(userRequestDto.userId(), userRequestDto.userName(), encryptedPassword, userRequestDto.role() );
        // 3.저장
        User savedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(savedUser);
    }


}
