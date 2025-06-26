package com.example.evently.reward.service.strategy;

import com.example.evently.event.domain.enums.RewardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RewardStrategyFactory {
    private final List<RewardStrategy> strategies;
    public RewardStrategy getStrategy(RewardType rewardType) {
        return strategies.stream()
                .filter(s -> s.supports(rewardType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 보상 타입입니다: " + rewardType));
    }

}
