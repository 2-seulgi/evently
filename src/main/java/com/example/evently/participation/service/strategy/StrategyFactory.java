package com.example.evently.participation.service.strategy;

import com.example.evently.event.domain.enums.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StrategyFactory {

    private final List<ParticipationStrategy> strategies;

    public ParticipationStrategy getStrategy(EventType eventType) {
        return strategies.stream()
                .filter(s -> s.supports(eventType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 이벤트 타입입니다: " + eventType));
    }
}
