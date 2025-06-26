package com.example.evently.reward.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.reward.dto.RewardResult;
import com.example.evently.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventRewardItemService {

    private final RewardStrategyFactory strategyFactory;

    @Transactional
    public RewardResult reward(Event event, User user) {
        RewardStrategy strategy = strategyFactory.getStrategy(event.getRewardType());
        return strategy.reward(event, user);
    }
}
