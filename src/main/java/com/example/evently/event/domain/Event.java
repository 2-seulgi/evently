package com.example.evently.event.domain;

import com.example.evently.event.domain.enums.EventType;
import com.example.evently.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(columnDefinition = "Text")
    private String description;
    @Column(nullable = false)
    private LocalDateTime startDate;
    @Column(nullable = false)
    private LocalDateTime endDate;
    @Column(nullable = false)
    private int pointReward;
    @Column(nullable = false)
    private int maxParticipants; //  최대 참여 가능 인원
    @Column(nullable = false)
    private int currentParticipants = 0; //  현재 참여자 수 (초기값 0)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventType eventType;
    @Column(nullable = false)
    private boolean isDeleted = false; //  삭제 여부 (Soft Delete)

    /** 객체 생성을 직접 제어 하기 위해 생성자 + 팩토리 메소드 사용( 유효성 검증 포함) **/
    // 생성자
    private Event(String title, String description, LocalDateTime startDate,
                  LocalDateTime endDate, int pointReward, EventType eventType) {
        validateEventDates(startDate, endDate);
        validatePointReward(pointReward);
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pointReward = pointReward;
        this.eventType = eventType;
    }

    // 팩토리 메소드 추가
    public static Event of(String title, String description, LocalDateTime startDate,
                           LocalDateTime endDate, int pointReward, EventType eventType) {
        return new Event(title, description, startDate, endDate, pointReward, eventType);
    }

    // 이벤트 수정
    public void updateEvent(String title, String description, LocalDateTime startDate,
                            LocalDateTime endDate, int pointReward) {
        validateEventDates(startDate, endDate);
        validatePointReward(pointReward);
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pointReward = pointReward ;
    }

    // 이벤트 날짜 유효성 검증
    private void validateEventDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작일과 종료일은 필수입니다.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("종료일은 시작일보다 이후여야 합니다.");
        }
    }

    // 포인트 유효성 검증
    private void validatePointReward(int pointReward) {
        if (pointReward < 0) {
            throw new IllegalArgumentException("포인트는 0보다 작을 수 없습니다.");
        }
    }

    // Soft Delete 처리 메소드 추가
    public void softDelete() {
        this.isDeleted = true;
    }

    /**
     * 이벤트 참여자 수 증가
     */
    public void increaseParticipants() {
        this.currentParticipants++;
    }

}
