package com.example.evently.participation.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.point.service.PointService;
import com.example.evently.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
// 출석체크 전략
public class AttendanceEventStrategy implements ParticipationStrategy {

    private final EventParticipationRepository participationRepository;
    private final PointService pointService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    private static final String PARTICIPATION_COUNT_KEY = "event:participants:";


    @Override
    public int participate(Event event, User user) {

        // 1일 1회 출석체크 유효성 체크
        validateCheckInToday(user, event);// 오늘 이미 참여 했으면 예외

        // Redis 에서 사용할 키 ( 현재 참여자 수 저장용)
        String key = PARTICIPATION_COUNT_KEY + user.getId();

        // 지급할 포인트 계산
        int point = event.getPointReward();

        // 동시성 제어를 하기 위해서 Redisson 분산 락을 획득함
        RLock lock = redissonClient.getLock(key); // 사용자 단위로 락 획득
        try{
            if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("이미 참여 중입니다.");
            }
            if (participationRepository.existsByUserAndEvent(user, event)) {
                throw new IllegalStateException("이미 참여한 이벤트입니다.");
            }
            // 이벤트 참여 엔티티 생성 및 저장
            EventParticipation participation = EventParticipation.builder()
                    .user(user)
                    .event(event)
                    .build();
            participationRepository.save(participation);
            // 참여자 수 증가( DB + Redis 캐시)
            event.increaseParticipants(); // JPA entity 내 필드 증가
            redisTemplate.opsForValue().increment(key); // Redis 캐시 값도 증가

            // 포인트 적립
            pointService.earnPoints(user, event, point, "출석 참여: " + event.getTitle());
            return point;
        } catch (InterruptedException e) {
            throw new RuntimeException("참여 중 오류가 발생했습니다.");
        } finally {
            lock.unlock(); // 락 해제
        }
    }

    @Override
    public boolean supports(EventType eventType) {
        return eventType == EventType.CHECKIN;
    }

    private void validateCheckInToday(User user, Event event) {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);

        boolean alreadyCheckedIn = participationRepository.existsByUserAndEventAndRegDateBetween(
                user, event, startOfToday, endOfToday
        );
        if (alreadyCheckedIn) {
            throw new IllegalStateException("오늘은 이미 출석체크를 완료했습니다.");
        }
    }
}
