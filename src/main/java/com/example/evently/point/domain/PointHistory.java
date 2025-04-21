package com.example.evently.point.domain;

import com.example.evently.event.domain.Event;
import com.example.evently.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 자동 생성
@AllArgsConstructor  // 모든 필드를 포함한 생성자 자동 생성
@Builder // 빌더 패턴으로 객체 생성
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_sn", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    private int points;
    @Column(name = "reason")
    private String reason;  // 포인트 적립/사용 이유
    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 적립/사용 날짜

    public static PointHistory earnPoints(User user, Event event, int points, String reason) {
        return PointHistory.builder()
                .user(user)
                .event(event)
                .points(points)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
