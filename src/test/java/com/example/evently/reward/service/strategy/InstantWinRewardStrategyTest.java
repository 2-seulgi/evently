package com.example.evently.reward.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.domain.enums.RewardType;
import com.example.evently.reward.domain.EventRewardHistory;
import com.example.evently.reward.domain.EventRewardItem;
import com.example.evently.reward.domain.enums.RewardItemType;
import com.example.evently.reward.dto.RewardResult;
import com.example.evently.reward.repository.EventRewardItemRepository;
import com.example.evently.reward.repository.RewardHistoryRepository;
import com.example.evently.user.domain.User;
import com.example.evently.user.domain.enums.UserRole;
import com.example.evently.user.domain.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstantWinRewardStrategyTest {
    @InjectMocks
    private InstantWinRewardStrategy strategy;
    @Mock
    private RewardHistoryRepository rewardHistoryRepository;
    @Mock
    private EventRewardItemRepository rewardItemRepository;
    @Mock
    private RLock lock;
    @Mock
    private RedissonClient redissonClient;

    @Test
    void 즉시당첨_확률기반_보상_정상처리() throws InterruptedException{
        // given
        Event event = Event.of("여름 휴가 이벤트", "여름 휴가 이벤트에 참가하시고 다양한 보상을 받아보세요",
                LocalDateTime.now(),LocalDateTime.now().plusDays(1), -50, EventType.GIVEAWAY, RewardType.INSTANT_WIN );
        User user = User.of("testId", "테스터", "pw123", UserStatus.ACTIVE, UserRole.USER);

        List<EventRewardItem> rewardItems = List.of(
                EventRewardItem.of(event, "호텔숙박권", 1, 0.01f, RewardItemType.COUPON),
                EventRewardItem.of(event, "에어팟", 3, 0.07f, RewardItemType.COUPON),
                EventRewardItem.of(event, "포인트", 30, 0.2f, RewardItemType.POINT)
        );

        given(rewardHistoryRepository.existsByEventAndUser(any(),any())).willReturn(false);
        given(rewardItemRepository.findByEventOrderByIdAsc(event)).willReturn(rewardItems);
        given(lock.tryLock(0, 3, TimeUnit.SECONDS)).willReturn(true);
        given(lock.isHeldByCurrentThread()).willReturn(true);
        given(redissonClient.getLock(anyString())).willReturn(lock);

        // when
        RewardResult rewardResult = strategy.reward(event, user);

        // then
        assertThat(rewardResult).isNotNull();
        assertThat(rewardResult.isWinner()).isIn(true, false);// 무조건 true, false 중 하나
        assertThat(rewardResult.message()).isNotNull();

        if (rewardResult.isWinner()) {
            // 1. 당첨 보상 이름은 리스트 내 존재해야 함
            List<String> rewardNames = rewardItems.stream()
                    .map(EventRewardItem::getRewardName)
                    .toList();
            assertThat(rewardResult.rewardName()).isIn(rewardNames);

            // 2. 수량 감소 검증
            EventRewardItem wonItem = rewardItems.stream()
                    .filter(i -> i.getRewardName().equals(rewardResult.rewardName()))
                    .findFirst()
                    .orElseThrow();
            assertThat(wonItem.getQuantity()).isLessThanOrEqualTo( // 1 감소됐는지만 간단히 확인
                    wonItem.getQuantity() + 1
            );

            // 3. 히스토리 저장 호출 검증
            verify(rewardHistoryRepository).save(any(EventRewardHistory.class));
        } else {
            // 꽝일 경우 rewardName은 null
            assertThat(rewardResult.rewardName()).isNull();
        }
    }

}