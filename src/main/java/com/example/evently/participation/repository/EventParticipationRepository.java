package com.example.evently.participation.repository;

import com.example.evently.event.domain.Event;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> , QuerydslPredicateExecutor<Event> {
    boolean existsByUserAndEvent(User user, Event event);
}
