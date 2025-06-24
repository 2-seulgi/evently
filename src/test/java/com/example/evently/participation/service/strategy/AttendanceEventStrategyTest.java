package com.example.evently.participation.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.point.service.PointService;
import com.example.evently.user.domain.User;
import com.example.evently.user.domain.enums.UserRole;
import com.example.evently.user.domain.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttendanceEventStrategyTest {
    @InjectMocks
    private AttendanceEventStrategy strategy;

    @Mock
    private EventParticipationRepository participationRepository;

    @Mock
    private PointService pointService;

    @Mock
    private  RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private Event event;
    private User user;

    @BeforeEach
    void setup() {
        event = Event.of("출석체크 이벤트", "6월 출석체크 이벤트", LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10, EventType.CHECKIN, null);
        user = User.of("testId", "테스터", "pw123", UserStatus.ACTIVE, UserRole.USER);
        lenient().when(redissonClient.getLock(anyString())).thenReturn(lock);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);  // redisTemplate이 opsForValue()를 호출하면, 가짜 ValueOperations 줌
        lenient().when(valueOperations.increment(anyString())).thenReturn(1L); // 그 다음에 increment() 호출되면 1L 리턴
    }

    @Test
    void 오늘_이미_출석한_경우_예외() {
        // given: 이미 출석했다고 응답하도록 설정
        given(participationRepository.existsByUserAndEventAndRegDateBetween(
                any(), any(), any(), any())
        ).willReturn(true);

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            strategy.participate(event, user); // 내부에서 validateCheckInToday 호출됨
        });
    }

    @Test
    void 락_획득_실패시_예외() throws InterruptedException {
        // given
        given(participationRepository.existsByUserAndEventAndRegDateBetween(
                any(),any(), any(), any()
        )).willReturn(false);
        given(redissonClient.getLock(anyString())).willReturn(lock);
        given(lock.tryLock(0, 5, TimeUnit.SECONDS)).willReturn(false); // 락 획득 실패
        // when & then
        assertThrows(IllegalStateException.class, () -> strategy.participate(event, user));
    }

    @Test
    void 출석_정상참여_성공() throws InterruptedException {
        // given
        given(participationRepository.existsByUserAndEventAndRegDateBetween(
                any(), any(), any(), any()
        )).willReturn(false); // 1일 1회 출석체크 - 아직 출석 안함
        given(lock.tryLock(0, 5, TimeUnit.SECONDS)).willReturn(true); // 락 획득 성공
        // 락 해제 조건(락을 내가 보유함)
        given(lock.isHeldByCurrentThread()).willReturn(true);

        // when
        int point = strategy.participate(event, user);

        // then
        assertThat(point).isEqualTo(event.getPointReward());
        verify(participationRepository).save(any(EventParticipation.class)); // 이벤트 참여 검증
        verify(redisTemplate.opsForValue()).increment(anyString()); // 참여자 수 증가 검증
        verify(pointService).earnPoints(eq(user), eq(event), eq(point), anyString());
        verify(lock).tryLock(0, 5, TimeUnit.SECONDS);
        verify(lock).unlock(); // 락 해제
    }

}