package com.example.evently.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http    //  프론트엔드에서 접근 허용할 출처(Origin) 설정
                .cors(cors -> cors.configurationSource(request -> {
                    var config  = new org.springframework.web.cors.CorsConfiguration();
                    config .setAllowedOrigins(List.of("http://localhost:5173")); // 개발 환경 (Vite 기본 포트) , Vue 앱 도메인
                    config .setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드 지정 (모든 기본 메서드 열어두기)
                    config .setAllowedHeaders(List.of("*"));// 모든 타입의 헤더 허용
                    config .setAllowCredentials(true);// 쿠키나 인증 정보 포함 허용
                    return config ;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) //  H2 콘솔 UI 표시 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
                        .requestMatchers("/events/**").permitAll() // 이벤트 등록용 임시로 추가
                        // .requestMatchers(HttpMethod.GET, "/events/**").permitAll() // 리스트 & 상세 조회는 허용
                        .requestMatchers("/auth/**").permitAll() // 로그인화면 및 로그인 인증 없이 가능
                        .requestMatchers("/auth").permitAll()  //
                        .requestMatchers("/auth/**").permitAll()
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

