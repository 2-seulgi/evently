package com.example.evently.reward.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.reward.dto.RewardResult;
import com.example.evently.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class DrawRewardStrategyTest {

    @InjectMocks
    private DrawRewardStrategy drawRewardStrategy;

    private Event event;
    private User user;

    @Test
    void 추첨_전에는_보상_없고_대기_상태만_반환() {
        RewardResult result = drawRewardStrategy.reward(event, user);

        assertThat(result.isWinner()).isFalse();
        assertThat(result.rewardName()).isNull();
        assertThat(result.message()).contains("추첨");
    }

}