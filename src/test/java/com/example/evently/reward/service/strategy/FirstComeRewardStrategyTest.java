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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FirstComeRewardStrategyTest {
    @InjectMocks
    private FirstComeRewardStrategy strategy;

    @Mock
    private RewardHistoryRepository rewardHistoryRepository;

    @Mock
    private EventRewardItemRepository rewardItemRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    private Event event;
    private User user;

    @Test
    void 보상_이미받은_사용자_예외발생(){
        // given
        event = Event.of("CU 3000원 쿠폰 선착순 이벤트", "선착순으로 쿠폰을 받아가세요", LocalDateTime.now(), LocalDateTime.now().plusDays(1), -500, EventType.GIVEAWAY, RewardType.INSTANT_WIN);
        ReflectionTestUtils.setField(event, "id", 1L);
        user = User.of("testId", "테스터", "pw123", UserStatus.ACTIVE, UserRole.USER);

        given(rewardHistoryRepository.existsByEventAndUser(any(),any())
        ).willReturn(true);

        // assert & then
        assertThrows(IllegalStateException.class,() -> strategy.reward(event, user));
    }

    @Test
    void 보상_아이템_남아있는경우_당첨처리() throws InterruptedException {
        // given
        event = Event.of("CU 3000원 쿠폰 선착순 이벤트", "선착순으로 쿠폰을 받아가세요", LocalDateTime.now(), LocalDateTime.now().plusDays(1), -500, EventType.GIVEAWAY, RewardType.INSTANT_WIN);
        ReflectionTestUtils.setField(event, "id", 1L);
        user = User.of("testId", "테스터", "pw123", UserStatus.ACTIVE, UserRole.USER);

        EventRewardItem item = EventRewardItem.of(event, "CU 3천원 쿠폰", 1, null, RewardItemType.COUPON);

        given(rewardHistoryRepository.existsByEventAndUser(any(),any())).willReturn(false);
        given(rewardItemRepository.findByEventOrderByIdAsc(event)).willReturn(List.of(item));
        given(lock.tryLock(0, 3, TimeUnit.SECONDS)).willReturn(true);
        given(lock.isHeldByCurrentThread()).willReturn(true);
        given(redissonClient.getLock(anyString())).willReturn(lock);

        //when
        RewardResult rewardResult = strategy.reward(event, user);

        //then
        assertThat(rewardResult.isWinner()).isTrue();
        assertThat(rewardResult.rewardName()).isEqualTo("CU 3천원 쿠폰");
        assertThat(rewardResult.message()).contains("당첨");

        // 수량 감소 확인
        assertThat(item.getQuantity()).isEqualTo(0);

        // 저장 호출 여부
        verify(rewardHistoryRepository).save(any(EventRewardHistory.class));
        verify(lock).unlock();
    }

    @Test
    void 보상_아이템_없는경우_실패처리(){
        // given
        event = Event.of("CU 3000원 쿠폰 선착순 이벤트", "선착순으로 쿠폰을 받아가세요", LocalDateTime.now(), LocalDateTime.now().plusDays(1), -500, EventType.GIVEAWAY, RewardType.INSTANT_WIN);
        ReflectionTestUtils.setField(event, "id", 1L);
        user = User.of("testId", "테스터", "pw123", UserStatus.ACTIVE, UserRole.USER);

        EventRewardItem item = EventRewardItem.of(event, "CU 3천원 쿠폰", 0, null, RewardItemType.COUPON);

        //when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, item::decreaseQuantity);
        assertThat(exception.getMessage()).contains("보상 수량이 부족합니다.");

    }

    @Test
    void 락_획득_실패시_예외발생()throws InterruptedException {
        // given
        event = Event.of("CU 3000원 쿠폰 선착순 이벤트", "선착순으로 쿠폰을 받아가세요", LocalDateTime.now(), LocalDateTime.now().plusDays(1), -500, EventType.GIVEAWAY, RewardType.INSTANT_WIN);
        ReflectionTestUtils.setField(event, "id", 1L);
        user = User.of("testId", "테스터", "pw123", UserStatus.ACTIVE, UserRole.USER);

        EventRewardItem item = EventRewardItem.of(event, "CU 3천원 쿠폰", 1, null, RewardItemType.COUPON);

        given(redissonClient.getLock(anyString())).willReturn(lock);
        given(lock.tryLock(0, 3, TimeUnit.SECONDS)).willReturn(false);

        // when & then
        assertThrows(IllegalStateException.class,() -> strategy.reward(event, user));

    }

}