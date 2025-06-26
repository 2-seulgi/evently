package com.example.evently.reward.repository;

import com.example.evently.event.domain.Event;
import com.example.evently.reward.domain.EventRewardItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRewardItemRepository extends JpaRepository<EventRewardItem, Long> {
    List<EventRewardItem> findByEventOrderByIdAsc(Event event);
}
