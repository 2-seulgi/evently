package com.example.evently.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // CSRF 비활성화 및 h2 콘솔에서는 보호 해제
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) //  H2 콘솔 UI 표시 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
                        .requestMatchers("/auth/**").permitAll() // 로그인화면 및 로그인 인증 없이 가능
                        .requestMatchers("/user").permitAll() // 회원 가입 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN") //관리자 API 보호
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); //  JWT 필터 적용

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}