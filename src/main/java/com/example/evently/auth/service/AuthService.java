package com.example.evently.auth.service;

import com.example.evently.global.util.JwtUtil;
import com.example.evently.user.domain.User;
import com.example.evently.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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


}
