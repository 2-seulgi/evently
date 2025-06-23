package com.example.evently.auth.service;

import com.example.evently.global.util.JwtUtil;
import com.example.evently.user.domain.User;
import com.example.evently.user.domain.enums.UserRole;
import com.example.evently.user.domain.enums.UserStatus;
import com.example.evently.user.dto.UserRequestDto;
import com.example.evently.user.dto.UserResponseDto;
import com.example.evently.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    /**
     * 로그인
     * @param loginId
     * @param password
     * @return
     */
    public Map<String, String> login(String loginId, String password) {
        if (loginId == null) {
            throw new IllegalArgumentException("loginId cannot be null");
        }
        loginId = loginId.trim(); // 공백 제거

        // 사용자 조회
        Optional<User> userOptional = userRepository.findByLoginId(loginId);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        User user = userOptional.get();

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        // JWT 토큰 생성 (권한 포함!)
        String token = jwtUtil.generateToken(user.getLoginId(), user.getUserRole());

        // 응답 맵 구성
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getUserRole().name());
        response.put("userId", String.valueOf(user.getId()));
        response.put("loginId", user.getLoginId());
        response.put("userName", user.getUserName());

        return response;
    }


    /**
     * 회원가입
     * @param userRequestDto
     * @return
     */
    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        // 1. 중복 검사
        if (userRepository.existsByLoginId(userRequestDto.loginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 2. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(userRequestDto.password());

        User user = User.of(
                userRequestDto.loginId(),
                userRequestDto.userName(),
                encryptedPassword,
                UserStatus.ACTIVE,
                UserRole.USER
        );// 여기서 고정 세팅

        // 3.저장
        User savedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(savedUser);
    }

}
