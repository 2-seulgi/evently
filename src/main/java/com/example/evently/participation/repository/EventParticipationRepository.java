package com.example.evently.participation.repository;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> , QuerydslPredicateExecutor<Event> {
    boolean existsByUserAndEvent(User user, Event event);
    // 특정 이벤트에 몇 명이 있는지 확인
    Integer countByEvent(Event event);

    boolean existsByUserAndEventAndRegDateBetween(User user, Event event, LocalDateTime startOfToday, LocalDateTime endOfToday);

    List<EventParticipation> findByUserIdAndEvent_EventTypeAndRegDate(Long userSn, EventType event_eventType, LocalDateTime regDate);
}
