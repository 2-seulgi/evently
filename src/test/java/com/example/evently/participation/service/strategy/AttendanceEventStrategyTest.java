package com.example.evently.participation.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.point.service.PointService;
import com.example.evently.user.domain.User;
import com.example.evently.user.domain.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisTemplate;

import java.security.cert.TrustAnchor;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

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

    private Event event;
    private User user;

    @BeforeEach
    void setup() {
        event = Event.of("출석", "출석이벤트", LocalDateTime.now(), LocalDateTime.now().plusDays(1), 100, EventType.CHECKIN);
        user = User.of("testId", "테스터", "pw123", UserRole.USER);
        lenient().when(redissonClient.getLock(anyString())).thenReturn(lock);
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


}