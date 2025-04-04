package com.example.evently.global.util;

import com.example.evently.user.domain.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // 생성자에게서 jwt 서명 키를 설정한다.  (application.yml에 설정된 secretKey 사용)
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime; // ✅ yml에서 가져옴

    private Key key;

    @PostConstruct 
    public void init() {
        // 🔐 secretKey를 기반으로 HMAC 서명 키 생성 , 의존성 주입 후 초기화 필요
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * jwt 토큰 생성 메소드
     * @param userId (로그인한 사용자 id)
     * @param role (사용자의 권한)
     * @return 생성된 jwt 토큰
     */
    public String generateToken(String userId, UserRole role) {
        return Jwts.builder()
                .setSubject(userId) // 토큰의 주인(-> 사용자)
                .claim("role", role.name()) // 역할 추가 (USER or ADMIN)
                .setIssuedAt(new Date()) // 토큰 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 설정(HMAC SHA-256)
                .compact();
    }

    /**
     * JWT 토큰 검증 메서드
     * @param token 클라이언트가 요청 시 전달한 JWT 토큰
     * @return 토큰에서 추출한 Claims (Payload 정보)
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 토큰의 서명을 검증할 키 설정
                .build()
                .parseClaimsJws(token) // jwt 토큰을 파싱하여 유효성 검사
                .getBody(); // 토큰의 payload 반환
    }


}


