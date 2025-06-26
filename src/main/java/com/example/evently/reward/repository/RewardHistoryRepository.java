package com.example.evently.reward.repository;

import com.example.evently.event.domain.Event;
import com.example.evently.reward.domain.EventRewardHistory;
import com.example.evently.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RewardHistoryRepository extends JpaRepository<EventRewardHistory, Long>, QuerydslPredicateExecutor<EventRewardHistory> {
    boolean existsByEventAndUser(Event event, User user);
}
