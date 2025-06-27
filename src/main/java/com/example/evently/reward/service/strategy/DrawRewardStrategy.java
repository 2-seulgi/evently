package com.example.evently.reward.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.RewardType;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.reward.dto.RewardResult;
import com.example.evently.reward.repository.RewardHistoryRepository;
import com.example.evently.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrawRewardStrategy implements RewardStrategy {
    private final RewardHistoryRepository rewardHistoryRepository;
    private final EventParticipationRepository eventParticipationRepository;

    @Override
    public boolean supports(RewardType rewardType) {
        return rewardType == RewardType.DRAW;
    }

    @Override
    public RewardResult reward(Event event, User user) {
        return RewardResult.pending(); // "이벤트 종료 후 추첨이 진행됩니다."
    }

}

