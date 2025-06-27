package com.example.evently.event.repository;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.domain.enums.RewardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findByEventTypeAndRewardTypeAndEndDateBefore(EventType eventType, RewardType rewardType, LocalDateTime now);
}
