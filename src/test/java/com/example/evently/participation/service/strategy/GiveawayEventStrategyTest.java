package com.example.evently.participation.service.strategy;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.domain.enums.RewardType;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GiveawayEventStrategyTest {
    @InjectMocks
    private GiveawayEventStrategy giveawayEventStrategy;

    @Mock
    private EventParticipationRepository participationRepository;

    @Mock
    private PointService pointService;

    private Event event;
    private User user;

    @BeforeEach
    void setup() {
        event = Event.of("경품 이벤트", "에어팟 추첨 이벤트",LocalDateTime.now(), LocalDateTime.now().plusDays(3),-50, EventType.GIVEAWAY, RewardType.DRAW);
        user = User.of("testId", "테스터", "pw123", UserStatus.ACTIVE, UserRole.USER);
    }

    @Test
    void 경품이벤트_정상참여_시_포인트차감된다() {

        // given
        user.updatePoints(100);
        given(participationRepository.existsByUserAndEvent(any(), any())).willReturn(false);

        // 포이늩 차감이 실제 적용되도록 동작을 정의함
        doAnswer(invocation -> {
            // user를 가져온다
            User u = invocation.getArgument(0);
            // 포인트를 가져온다
            int p = invocation.getArgument(2);
            //  포인트를 직접 차감 (u의 포인트를 p만큼 조정)
            u.updatePoints(u.getPoints() + p);
            // void라서
            return null;
        }).when(pointService).earnPoints(any(), any(), anyInt(), anyString());

        // when
        int result = giveawayEventStrategy.participate(event, user);

        // then
        assertThat(result).isEqualTo(-50);// 이벤트 차감 포인트
        verify(participationRepository).save(any());
        verify(pointService).earnPoints(eq(user), eq(event), eq(-50), anyString());

        // 차감 확인
        assertThat(user.getPoints()).isEqualTo(50);
    }

    @Test
    void 중복_참여시_예외발생(){
        // given : 이미 참여 했다고 응답하도록 설정
        given(participationRepository.existsByUserAndEvent(any(), any())).willReturn(true);

        // when&then
        IllegalStateException exception = assertThrows(IllegalStateException.class, ()->{
            giveawayEventStrategy.participate(event, user);
        });
        assertThat(exception.getMessage()).isEqualTo("이미 해당 이벤트에 참여한 사용자입니다.");
    }

    @Test
    void 포인트부족_시_예외발생(){
        //given
        user.updatePoints(10);
        given(participationRepository.existsByUserAndEvent(any(), any())).willReturn(false);

        //when&then
        IllegalStateException exception =assertThrows(IllegalStateException.class, ()->{
            giveawayEventStrategy.participate(event, user);
        });
        assertThat(exception.getMessage()).isEqualTo("보유 포인트가 부족하여 참여할 수 없습니다.");
    }

}