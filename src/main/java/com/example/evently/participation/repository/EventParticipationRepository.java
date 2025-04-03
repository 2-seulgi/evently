package com.example.evently.participation.repository;

import com.example.evently.event.domain.Event;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> , QuerydslPredicateExecutor<Event> {
    boolean existsByUserAndEvent(User user, Event event);
    // 특정 이벤트에 몇 명이 있는지 확인
    Integer countByEvent(Event event);

    boolean existsByUserAndEventAndParticipationDateBetween(User user, Event event, LocalDateTime startOfToday, LocalDateTime endOfToday);
}
