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
        RLock lock = null;
        try{
            lock = redissonClient.getLock(key); // 사용자 단위로 락 획득
            // waitTime은 락이 풀릴 때까지 기다리는 시간, leaseTime 락을 점유할 수 있는 시간
            // 출석 체크의 경우 중복체크가 중요하기 때문에 대기시간 짧아도 괜찮다고 판단해서 0으로 잡음
            if (!lock.tryLock(0, 5, TimeUnit.SECONDS)) { //락을 바로 시도하고 얻는 경우 5초 보유
                throw new IllegalStateException("이미 참여 중입니다.");
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
            // null이 아니고, 내가 락을 가지고 있을 때 해제
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean supports(EventType eventType) {
        return eventType == EventType.CHECKIN;
    }

    /**
     * 오늘 출석체크 했는지 확인
     * @param user
     * @param event
     */
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
