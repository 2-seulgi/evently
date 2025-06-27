package com.example.evently.reward.service.draw;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.domain.enums.RewardType;
import com.example.evently.event.repository.EventRepository;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.participation.domain.enums.RewardStatus;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.reward.domain.EventRewardHistory;
import com.example.evently.reward.domain.EventRewardItem;
import com.example.evently.reward.repository.EventRewardItemRepository;
import com.example.evently.reward.repository.RewardHistoryRepository;
import com.example.evently.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DrawRewardProcessor {

    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRewardItemRepository eventRewardItemRepository;
    private final RewardHistoryRepository rewardHistoryRepository;


    @Transactional
    public void processDrawRewards(){
        // 보상이 필요한 종료된 DRAW 이벤트 조회
        List<Event> endedDrawEvents = eventRepository.findByEventTypeAndRewardTypeAndEndDateBefore(
                EventType.GIVEAWAY,
                RewardType.DRAW,
                LocalDateTime.now()
        );
        for(Event endedDrawEvent : endedDrawEvents){
            // 해당 이벤트에 참여한 모든 유저 조회
            List<EventParticipation> participants = eventParticipationRepository.findByEvent(endedDrawEvent);

            // 참여자 순서를 랜덤 섞기
            Collections.shuffle(participants);

            // 보상 아이템 목록 조회(확률 낮은 순 → 좋은 보상 먼저)
            List<EventRewardItem> rewardItems = eventRewardItemRepository.findByEventOrderByIdAsc(endedDrawEvent);
            rewardItems.sort(Comparator.comparing(EventRewardItem::getProbability)); // 낮은 확률 = 좋은 보상

            // 당첨된 유저 ID 추적 (중복 방지)
            Set<Long> rewardedUserIds = new HashSet<>();

            // 아이템별 당첨자 수령
            for(EventRewardItem rewardItem : rewardItems){
                int winnersNeeded = rewardItem.getQuantity();
                int winnersAssigned = 0;
                for(EventParticipation participation : participants){
                    if(winnersAssigned >= winnersNeeded) break;

                    User user = participation.getUser();
                    if(rewardedUserIds.contains(user.getId())) continue;

                    // 당첨 확률에 따라 랜덤 추첨을 수행 예) 보상 확률이 0.2f 라면 20퍼 확률로 if 문을 통과
                    // Math.random()은 0.0 ~ 1.0 사이의 실수를 무작위로 생성하므로,
                    // 생성된 난수가 보상 확률 이하일 때만 당첨 처리
                    if((float)Math.random() <= rewardItem.getProbability()){
                        // 당첨처리
                        // rewardItem.decreaseQuantity();
                        EventRewardHistory history = EventRewardHistory.of(
                                endedDrawEvent,
                                user,
                                RewardType.DRAW ,
                                rewardItem.getRewardName(),
                                RewardStatus.WIN
                        );
                        rewardHistoryRepository.save(history);

                        rewardedUserIds.add(user.getId());
                        winnersAssigned++;
                    }
                }
            }
        }
    }
}
