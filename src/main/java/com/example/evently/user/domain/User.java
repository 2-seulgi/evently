package com.example.evently.user.domain;

import com.example.evently.global.entity.BaseEntity;
import com.example.evently.user.domain.enums.UserRole;
import com.example.evently.user.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId; // 사용자 아이디
    @Column(name ="user_name", nullable = false)
    private String userName;  // 사용자 이름
    @Column(name="password", nullable = false)
    private String password;  // 비밀번호
    @Column(name="points",nullable = false)
    private int points = 0;   // 사용자의 현재 포인트
    @Column(name="user_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    @Column(name="is_use",nullable = false)
    private boolean isUse = true;   // true/false
    @Enumerated(EnumType.STRING)
    @Column(name="user_role",nullable = false)
    private UserRole userRole;

    // 생성자
    private User(String loginId, String userName, String password, UserStatus userStatus, UserRole userRole) {
        this.loginId = loginId;
        this.userName = userName;
        this.password = password;
        this.userStatus = userStatus;
        this.userRole = userRole;
        this.points = 0;
    }

    //팩토리 메소드 사용
    public static User of(String loginId, String userName, String password, UserStatus userStatus, UserRole userRole) { // ✅ UserRole을 직접 받도록 수정
        return new User(loginId, userName, password, userStatus, userRole);
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

    public void updateUser(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public void softDelete() {
        this.isUse = false;
        this.userStatus = UserStatus.WITHDRAWN; // valueOf 안 써도 됨
    }

}
