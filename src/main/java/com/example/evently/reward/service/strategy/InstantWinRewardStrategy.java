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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InstantWinRewardStrategy implements RewardStrategy {
    private final RewardHistoryRepository rewardHistoryRepository;
    private final EventRewardItemRepository rewardItemRepository;

    private final RedissonClient redissonClient;

    @Override
    public boolean supports(RewardType rewardType) {
        return rewardType == RewardType.INSTANT_WIN;
    }

    @Override
    public RewardResult reward(Event event, User user) {
        // 이미 보상을 받은 이력이 있다면 예외
        if (rewardHistoryRepository.existsByEventAndUser(event, user)) {
            throw new IllegalStateException("이미 보상을 받은 사용자 입니다. ");
        }

        RLock lock = redissonClient.getLock("reward:event:" + event.getId());
        try {
            boolean available = lock.tryLock(0, 3, TimeUnit.SECONDS); // wait 0s, hold 5s
            if (!available) {
                throw new IllegalStateException("잠시 후 다시 시도해주세요."); //
            }
            List<EventRewardItem> availableItems = rewardItemRepository.findByEventOrderByIdAsc(event);

            // 확률 기반으로 당첨 아이템 선택
            EventRewardItem selectedItem = selectByProbability(availableItems);

            // 꽝 처리
            if (selectedItem == null || !selectedItem.isAvailable()) return RewardResult.lose(null);

            // 수량 차감 + 히스토리 저장
            selectedItem.decreaseQuantity();
            EventRewardHistory history = EventRewardHistory.of(
                    event,
                    user,
                    RewardType.INSTANT_WIN,
                    selectedItem.getRewardName(),
                    RewardStatus.WIN
            );
            rewardHistoryRepository.save(history);

            return new RewardResult(true, selectedItem.getRewardName(), "축하합니다! 당첨되었습니다 🎉");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 처리
            throw new RuntimeException("참여 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock(); // 항상 락 해제
            }
        }
    }

    /**
     * 확률에 따라 하나의 보상 아이템을 랜덤으로 선택하는 메서드.
     * @param items
     * @return
     */
    private EventRewardItem selectByProbability(List<EventRewardItem> items){
        float randomvalue = (float) Math.random();// 0.0~1.0
        float cumulative = 0f; // 누적 확률 값을 저장

        // 보상 아이템을 순회 하면서 확률을 누적
        for(EventRewardItem item : items){
            if(!item.isAvailable())continue; // 수량이 0이면 보상에서 제외
            if(item.getProbability() == null || item.getProbability() <= 0)continue; // 확률이 없거나 0보다 작으면 제외
            cumulative += item.getProbability(); // 누적 확률에 현재 아이템의 확률을 더함
            if(randomvalue < cumulative)return item; // 생성한 랜덤값이 누적 확률보다 작거나 같으면 당첨
        }
        return null;
    }
}
