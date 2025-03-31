package com.example.evently.auth;

import com.example.evently.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Getter
public class CustomUserDetails implements UserDetails {

    private final User user; // 실제 유저 엔티티

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 권한 반환 (예: ROLE_USER, ROLE_ADMIN)
        return Collections.singleton(() -> "ROLE_" + user.getUserRole().name());
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // 실제 비밀번호 (로그인 시만 사용됨)
    }

    @Override
    public String getUsername() {
        return user.getUserId(); // 사용자 ID (로그인용)
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
