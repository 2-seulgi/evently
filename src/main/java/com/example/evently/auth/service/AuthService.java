package com.example.evently.auth.service;

import com.example.evently.global.util.JwtUtil;
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
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    /**
     * 로그인
     * @param userId
     * @param password
     * @return
     */
    public String login(String userId, String password) {

        //1. 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다."));

        // 2. 비밀번호 검증
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다");
        }

        // 3. jwt 토큰 발급
        return jwtUtil.generateToken(user.getUserId(), user.getRole());
    }

    /**
     * 회원가입
     * @param userRequestDto
     * @return
     */
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
