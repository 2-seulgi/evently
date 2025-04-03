package com.example.evently.participation.service;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.repository.EventRepository;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.participation.dto.EventParticipantResponseDto;
import com.example.evently.participation.dto.EventParticipationRequestDto;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.participation.repository.EventParticipationQueryRepository;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.point.service.PointService;
import com.example.evently.user.domain.User;
import com.example.evently.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventParticipationQueryRepository eventParticipationQueryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient; // 분산 락

    private static final String PARTICIPATION_COUNT_KEY = "event:participation:";

    /**
     * 이벤트참여
     * @param eventId
     * @param userSn
     */
    @Transactional
    public void participate(Long eventId, Long userSn) {
        // 사용자 조회
        User user = userRepository.findById(userSn)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        // 이벤트 조회
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트를 찾을 수 없습니다."));

        String key = PARTICIPATION_COUNT_KEY + user.getId();

        // 참여자 수 확인
        Integer participationCount = (Integer) redisTemplate.opsForValue().get(key);  // Redis에서 참여자 수 조회
        if (participationCount == null) {
            participationCount = eventParticipationRepository.countByEvent(event); // DB 조회
            redisTemplate.opsForValue().set(key, participationCount, Duration.ofMinutes(30)); //(캐시에 데이터가 있으면 DB를 조회하지 않고 Redis 데이터를 사용)
        }

        if (event.getEventType() == EventType.CHECKIN) {
            validateCheckInToday(user, event);
        } else {
            // 이벤트 최대 참여 인원 초과 확인
            if (participationCount >= event.getMaxParticipants() ) {
                throw new IllegalStateException("이벤트가 마감되었습니다.");
            }
        }

        // 중복 참여 여부 확인 -> 락을 사용해서 동시성 체크(한번에 하나의 쓰레드만 특정 코드 블록을 실행)
        RLock lock = redissonClient.getLock("event_lock:" + eventId);
        try{
            if(lock.tryLock(5,10, TimeUnit.SECONDS)){ // 최대 5초간 락을 획득하고, 락을 획득하면 10초 동안 다른 쓰레드가 접근하지 못하게 함
                boolean alreadyParticipated = eventParticipationRepository.existsByUserAndEvent(user, event); // 특정 사용자의 특정 이벤트 참여 확인

                if(alreadyParticipated){
                    throw new IllegalStateException("이미 참여한 이벤트 입니다.");
                }

                // 이벤트 참여 저장
                EventParticipation eventParticipation = EventParticipation.builder()
                        .user(user)
                        .event(event)
                        .build();
                eventParticipationRepository.save(eventParticipation);

                //redis에서 참여자 수 증가
                event.increaseParticipants();
                eventRepository.save(event);
                redisTemplate.opsForValue().increment(key);

                // 포인트 지급
                pointService.earnPoints(user, event.getPointReward(), "이벤트 참여: " + event.getTitle());
            }else{
                throw new IllegalStateException("잠시 후 다시 시도해주세요.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("이벤트 참여 중 오류가 발생했습니다.");
        } finally {
            lock.unlock();
        }
    }

    private void validateCheckInToday(User user, Event event) {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);

        boolean alreadyCheckedIn = eventParticipationRepository.existsByUserAndEventAndParticipationDateBetween(
                user, event, startOfToday, endOfToday
        );

        if (alreadyCheckedIn) {
            throw new IllegalStateException("오늘은 이미 출석체크를 완료했습니다.");
        }
    }

    /**
     * 사용자의 참석 이벤트 조회
     * @param userSn
     * @param eventName
     * @param startDate
     * @param endDate
     * @param page
     * @param size
     * @return
     */
    public Page<EventParticipationResponseDto> getUserParticipationHistory(Long userSn, String eventName, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "regDate")); //페이지 번호를 0부터 시작하도록 변환 (Spring Data JPA는 0-based index 사용)
        return eventParticipationQueryRepository.findUserParticipationHistory(userSn, eventName, startDate, endDate, pageable);
    }

    public Page<EventParticipantResponseDto> getParticipantsByEvent(Long eventId, String userName, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "regDate"));
        return eventParticipationQueryRepository.findParticipantsByEventId(eventId, userName, pageable);
    }


}
