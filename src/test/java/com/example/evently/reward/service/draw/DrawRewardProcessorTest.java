package com.example.evently.reward.service.draw;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.domain.enums.RewardType;
import com.example.evently.event.repository.EventRepository;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.reward.domain.EventRewardHistory;
import com.example.evently.reward.domain.EventRewardItem;
import com.example.evently.reward.domain.enums.RewardItemType;
import com.example.evently.reward.repository.EventRewardItemRepository;
import com.example.evently.reward.repository.RewardHistoryRepository;
import com.example.evently.user.domain.User;
import com.example.evently.user.domain.enums.UserRole;
import com.example.evently.user.domain.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DrawRewardProcessorTest {
    @InjectMocks
    private DrawRewardProcessor drawRewardProcessor;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventRewardItemRepository eventRewardItemRepository;

    @Mock
    private RewardHistoryRepository rewardHistoryRepository;


    private Event event;
    private User user;

    @BeforeEach
    void setup() {
        event = Event.of("럭키드로우", "럭키드로우에 참가하고 다양한 상품을 받아가세요", LocalDateTime.now().minusDays(1), LocalDateTime.now(), -30, EventType.GIVEAWAY, RewardType.DRAW);
        ReflectionTestUtils.setField(event, "id", 1L);
        user = User.of("testId", "테스터", "pw123", UserStatus.ACTIVE, UserRole.USER);

    }

    @Test
    void 이미_보상이_완료된_이벤트는_패스(){
        // given: 이벤트가 이미 처리
        given(eventRepository.findByEventTypeAndRewardTypeAndEndDateBefore(
                any(), any(), any()
        )).willReturn(List.of(event)); // 테스트 대상 이벤트 반환
        given(rewardHistoryRepository.existsByEvent(event)).willReturn(true); // 이미 보상된 상태

        //when
        drawRewardProcessor.processDrawRewards();

        // then: save가 단 한 번도 호출되지 않음
        verify(rewardHistoryRepository, never()).save(any());

    }

    @Test
    void 보상이_필요한_이벤트에대해_저장(){
        //given
        User participantUser = User.of("tester1", "테스트유저", "1234", UserStatus.ACTIVE, UserRole.USER);
        EventParticipation participation = EventParticipation.builder()
                .user(participantUser)
                .event(event)
                .build();
        given(eventRepository.findByEventTypeAndRewardTypeAndEndDateBefore(
                any(), any(), any()
        )).willReturn(List.of(event)); // 이벤트가 1개 있다고 가정
        given(rewardHistoryRepository.existsByEvent(event)).willReturn(false); // 아직 보상 안 함
        given(eventParticipationRepository.findByEvent(event)).willReturn(List.of(participation));
        given(eventRewardItemRepository.findByEventOrderByIdAsc(event)).willReturn(
                new ArrayList<>(List.of(
                        EventRewardItem.of(event, "테스트용 확정 보상", 1, 0.7f, RewardItemType.ITEM),
                        EventRewardItem.of(event, "호텔숙박권", 1, 0.01f, RewardItemType.ITEM),
                        EventRewardItem.of(event, "아이패드", 2, 0.05f, RewardItemType.ITEM),
                        EventRewardItem.of(event, "에어팟", 5, 0.07f, RewardItemType.ITEM),
                        EventRewardItem.of(event, "포인트", 30, 0.2f, RewardItemType.POINT)
            ))
        );

        //when
        drawRewardProcessor.processDrawRewards();

        // then
        verify(rewardHistoryRepository).save(any(EventRewardHistory.class));
    }


}