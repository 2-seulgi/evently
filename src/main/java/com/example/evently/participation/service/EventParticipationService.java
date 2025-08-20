package com.example.evently.participation.service;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.repository.EventRepository;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.participation.dto.EventParticipantResponseDto;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.participation.dto.ParticipationResponseDto;
import com.example.evently.participation.repository.EventParticipationQueryRepository;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.participation.service.strategy.ParticipationStrategy;
import com.example.evently.participation.service.strategy.StrategyFactory;
import com.example.evently.reward.dto.RewardResult;
import com.example.evently.reward.service.strategy.EventRewardItemService;
import com.example.evently.user.domain.User;
import com.example.evently.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StrategyFactory strategyFactory;

    private final EventParticipationRepository eventParticipationRepository;
    private final EventParticipationQueryRepository eventParticipationQueryRepository;
    private final EventRewardItemService eventRewardItemService;

    /**
     * 이벤트 참가
     * @param eventId
     * @param userId
     * @return
     */
    @Transactional
    public ParticipationResponseDto participateInEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(()->new IllegalArgumentException("이벤트를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        // 이벤트 타입에 따라 전략 선택 후 참여 로직을 위임함
        ParticipationStrategy strategy = strategyFactory.getStrategy(event.getEventType());
        int pointReward = strategy.participate(event, user);
        RewardResult rewardResult = eventRewardItemService.reward(event, user); // 즉시당첨/선착순일 경우만 의미 있음

        return new ParticipationResponseDto(
                pointReward,
                event.getEventType(),
                event.getRewardType(),
                rewardResult // ✨ 보상 결과 추가
        );
    }

    /**
     * 사용자의 참석 이벤트 조회
     * @param userId
     * @param eventName
     * @param startDate
     * @param endDate
     * @param page
     * @param size
     * @return
     */
    public Page<EventParticipationResponseDto> getUserParticipationHistory(Long userId, String eventName, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page , size, Sort.by(Sort.Direction.DESC, "regDate")); //페이지 번호를 0부터 시작하도록 변환 (Spring Data JPA는 0-based index 사용)
        return eventParticipationQueryRepository.findUserParticipationHistory(userId, eventName, startDate, endDate, pageable);
    }

    /**
     * 관리자 > 이벤트별 참가자 조회
     * @param eventId
     * @param userName
     * @param page
     * @param size
     * @return
     */
    public Page<EventParticipantResponseDto> getParticipantsByEvent(Long eventId, String userName, int page, int size) {
        Pageable pageable = PageRequest.of(page , size, Sort.by(Sort.Direction.DESC, "regDate"));
        return eventParticipationQueryRepository.findParticipantsByEventId(eventId, userName, pageable);
    }


    /**
     * 오늘 기준 출석체크 확인
     * @param userId
     * @return
     */
    public List<Long> getTodayCheckInEventIds(Long userId) {
        List<EventParticipation> participation = eventParticipationRepository.findByUserIdAndEvent_EventTypeAndRegDate(
                userId, EventType.CHECKIN, LocalDateTime.now());
        return participation.stream()
                .map(p->p.getEvent().getId()) //각 참여 기록 EventParticipation 객체에서 그 안에 있는 Event의 ID만 추출
                .distinct()
                .toList();
    }

}
