package com.example.evently.reward.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.RewardType;
import com.example.evently.participation.domain.enums.RewardStatus;
import com.example.evently.reward.domain.EventRewardHistory;
import com.example.evently.reward.domain.EventRewardItem;
import com.example.evently.reward.dto.RewardResult;
import com.example.evently.reward.repository.EventRewardItemRepository;
import com.example.evently.reward.repository.RewardHistoryRepository;
import com.example.evently.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FirstComeRewardStrategy implements RewardStrategy {
    private final RewardHistoryRepository rewardHistoryRepository;
    private final EventRewardItemRepository rewardItemRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public boolean supports(RewardType rewardType) {
        return rewardType == RewardType.FIRST_COME_FIRST_SERVED;
    }

    @Override
    public RewardResult reward(Event event, User user) {
        // 이미 보상을 받은 이력이 있다면 예외
        if (rewardHistoryRepository.existsByEventAndUser(event, user)) {
            throw new IllegalStateException("이미 보상을 받은 사용자 입니다. ");
        }

        RLock lock = redissonClient.getLock("reward:event:" + event.getId());

        try {
            boolean available = lock.tryLock(0, 3, TimeUnit.SECONDS);
            if (!available) {
                throw new IllegalStateException("잠시 후 다시 시도해주세요."); //
            }

            // 🔒 락 획득 성공했을 때만 이 아래 로직 수행
            List<EventRewardItem> availableItems = rewardItemRepository.findByEventOrderByIdAsc(event);

            for (EventRewardItem item : availableItems) {
                if (item.isAvailable()) {
                    // 해당 보상 아이템이 이미 최대 수량만큼 지급되었는지 확인
                    int assignedCount = rewardHistoryRepository.countByEventAndRewardName(event, item.getRewardName());
                    if (assignedCount >= item.getQuantity()) {
                        return RewardResult.lose(null); // 수량 초과로 꽝 처리
                    }
                    //item.decreaseQuantity(); // 수량 차감

                    EventRewardHistory history = EventRewardHistory.of(
                            event,
                            user,
                            RewardType.FIRST_COME_FIRST_SERVED,
                            item.getRewardName(),
                            RewardStatus.WIN
                    );
                    rewardHistoryRepository.save(history);

                    return new RewardResult(true, item.getRewardName(), "축하합니다! 보상에 당첨되었습니다!");
                }
            }

            return new RewardResult(false, "아쉽지만 당첨되지 않았습니다.", null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 처리
            throw new RuntimeException("참여 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock(); // 항상 락 해제
            }
        }
    }
}
