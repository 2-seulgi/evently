package com.example.evently.reward.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.RewardType;
import com.example.evently.reward.dto.RewardResult;
import com.example.evently.user.domain.User;

public interface RewardStrategy {
    boolean supports(RewardType rewardType);
    RewardResult reward(Event event, User user);

}
