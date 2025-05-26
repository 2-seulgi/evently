package com.example.evently.participation.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.user.domain.User;

public interface ParticipationStrategy {
    int participate(Event event, User user); // 참여 후 포인트 반환
    boolean supports(EventType eventType);
}
