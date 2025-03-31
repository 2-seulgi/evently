package com.example.evently.global.config;

import com.example.evently.auth.CustomUserDetails;
import com.example.evently.auth.service.CustomUserDetailsService;
import com.example.evently.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if(token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);// Bearer 부분 제거

            // JWT 토큰 파싱 -> 클레임 추출
            Claims claims = jwtUtil.parseToken(token);
            String userId = claims.getSubject();//사용자ID(이메일)

            // userId로 DB 조회 → User 엔티티 → CustomUserDetails 생성
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(userId);

            // Spring Security의 Authentication 객체 생성 및 등록
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

}
