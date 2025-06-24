package com.example.evently.participation.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.point.service.PointService;
import com.example.evently.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GiveawayEventStrategy implements ParticipationStrategy {
    private final EventParticipationRepository participationRepository;
    private final PointService pointService;

    @Override
    public int participate(Event event, User user) {
        // 이벤트 타입 확인
        if (event.getEventType() != EventType.GIVEAWAY) {
            throw new IllegalArgumentException("경품 이벤트가 아닙니다.");
        }

        // 중복 참여 검사
        validateDuplicate(user, event);

        // 차감 전 사용자 포인트가 부족한지 확인
        int point = -Math.abs(event.getPointReward()); // 항상 음수
        if(user.getPoints() + point < 0) {
            throw new IllegalStateException("보유 포인트가 부족하여 참여할 수 없습니다.");
        }

        // 이벤트 참여 엔티티 생성 및 저장
        EventParticipation participation = EventParticipation.builder()
                .user(user)
                .event(event)
                .build();
        // 참여 기록만 남김 (보상은 별도)
        participationRepository.save(participation);
        pointService.earnPoints(user, event, point, "경품 이벤트 참여: " + event.getTitle());

        return point;
    }

    @Override
    public boolean supports(EventType eventType) {
        return eventType == EventType.GIVEAWAY;
    }

    /**
     * 이미 참여한 이력이 있는지 검사
     * @param user
     * @param event
     */
    private void validateDuplicate(User user, Event event) {
        boolean alreadyParticipated = participationRepository.existsByUserAndEvent(user, event);
        if (alreadyParticipated) {
            throw new IllegalStateException("이미 해당 이벤트에 참여한 사용자입니다.");
        }
    }
}
