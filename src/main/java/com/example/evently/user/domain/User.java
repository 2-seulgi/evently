package com.example.evently.user.domain;

import com.example.evently.global.entity.BaseEntity;
import com.example.evently.user.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.EnumSet;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String userId; // 사용자 아이디
    @Column(nullable = false)
    private String userName;  // 사용자 이름
    @Column(nullable = false)
    private String password;  // 비밀번호
    @Column(nullable = false)
    private int points = 0;   // 사용자의 현재 포인트
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    // 생성자
    private User(String userId, String userName, String password, UserRole userRole) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.userRole = userRole;
        this.points = 0;
    }

    //팩토리 메소드 사용
    public static User of(String userId, String userName, String password, UserRole userRole) { // ✅ UserRole을 직접 받도록 수정
        return new User(userId, userName, password, userRole);
    }

    // 포인트 업데이트
    public void updatePoints(int points)
    {
        this.points = points;
    }

    // 암호화
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
