package com.example.evently.participation.service;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.EventType;
import com.example.evently.event.repository.EventRepository;
import com.example.evently.participation.dto.EventParticipantResponseDto;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.participation.repository.EventParticipationQueryRepository;
import com.example.evently.participation.service.strategy.AttendanceEventStrategy;
import com.example.evently.participation.service.strategy.StrategyFactory;
import com.example.evently.user.domain.User;
import com.example.evently.user.domain.enums.UserRole;
import com.example.evently.user.domain.enums.UserStatus;
import com.example.evently.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventParticipationQueryRepository eventParticipationQueryRepository;

    @Mock
    private StrategyFactory strategyFactory;

    @Mock
    AttendanceEventStrategy attendanceStrategy;

    private Event event;
    private User user;
    @BeforeEach
    void setup() {
        event = Event.of("출석체크 이벤트", "6월 출석체크 이벤트", LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10, EventType.CHECKIN);
        user = User.of("testId", "테스터", "pw123", UserStatus.ACTIVE, UserRole.USER);
    }

    @Test
    void 이벤트_참여시_출석_전략_정상호출() {

        //given
        given(eventRepository.findById(anyLong())).willReturn(Optional.of(event));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(strategyFactory.getStrategy(EventType.CHECKIN)).willReturn(attendanceStrategy);// 전략 팩토리로부터 해당 전략 리턴하도록 설정
        given(attendanceStrategy.participate(any(), any())).willReturn(10);

        //when
        int point = eventParticipationService.participateInEvent(1L, 1L);

        //then
        assertThat(point).isEqualTo(10);
        verify(attendanceStrategy).participate(eq(event), eq(user));

    }

    @Test
    void 참석한_이벤트_조회() {

        //given
        // 테스트용 페이징 정보와 dto 생성
        Pageable pageable = PageRequest.of(0 , 10, Sort.by(Sort.Direction.DESC, "regDate"));
        // 응답 DTo 실제 참여 내역
        EventParticipationResponseDto dto = new EventParticipationResponseDto(
                1L,                          // eventId
                "출석체크 이벤트",               // eventName
                LocalDateTime.now()               // 참여일시
        );
        // 가짜 응답 page 객체 생성(1건 참여 내역)
        Page<EventParticipationResponseDto> mockPage= new PageImpl<>(Collections.singletonList(dto));

        // repository 호출 시 이 mockPage를 리턴하도록 설정
        given(eventParticipationQueryRepository.findUserParticipationHistory(
                eq(1L),       // 사용자 ID
                anyString(),  // 이벤트 이름 (anyString으로 처리)
                any(),        // 시작일 (LocalDateTime)
                any(),        // 종료일
                eq(pageable)  // 페이징 정보
        )).willReturn(mockPage);

        // when : 실제 서비스 메서드 호출
        Page<EventParticipationResponseDto> result =
                eventParticipationService.getUserParticipationHistory(
                        1L,                          // userSn
                        "출석",                       // eventName (검색용)
                        LocalDateTime.now().minusDays(3), // 검색 시작일
                        LocalDateTime.now(),              // 검색 종료일
                        0,                           // page
                        10                           // size
                );

        // then
        // 응답 데이터의 크기가 1인지 확인
        assertThat(result.getContent()).hasSize(1);
        // 첫 번째 응답 데이터의 eventName이 예상값과 일치하는지 확인
        assertThat(result.getContent().get(0).eventTitle()).isEqualTo("출석체크 이벤트");
        // Repository의 해당 메서드가 정확히 한 번 호출되었는지 검증
        verify(eventParticipationQueryRepository).findUserParticipationHistory(
                eq(1L), anyString(), any(), any(), eq(pageable));

    }

    @Test
    void 이벤트별_참가자_조회() {
        //given
        // 테스트용 페이징 정보와 dto 생성
        Pageable pageable = PageRequest.of(0 , 10, Sort.by(Sort.Direction.DESC, "regDate"));
        // 응답 DTo 실제 참여 내역
        EventParticipantResponseDto dto = new EventParticipantResponseDto(
                1L,                          // userSn
                "테스터",               // userName
                LocalDateTime.now()               // 참여일시
        );
        // 가짜 응답 page 객체 생성(1건 참여 내역)
        Page<EventParticipantResponseDto> mockPage= new PageImpl<>(Collections.singletonList(dto));

        // repository 호출 시 이 mockPage를 리턴하도록 설정
        given(eventParticipationQueryRepository.findParticipantsByEventId(
                eq(1L),       // 사용자 Sn
                anyString(),  // 유저 이름 (anyString으로 처리)
                eq(pageable)  // 페이징 정보
        )).willReturn(mockPage);

        // when : 실제 서비스 메서드 호출
        Page<EventParticipantResponseDto> result =
                eventParticipationService.getParticipantsByEvent(
                        1L,                          // userSn
                        "테스터",                       // userName (검색용)
                        0,                           // page
                        10                           // size
                );

        // then
        // 응답 데이터의 크기가 1인지 확인
        assertThat(result.getContent()).hasSize(1);
        // 첫 번째 응답 데이터의 eventName이 예상값과 일치하는지 확인
        assertThat(result.getContent().get(0).userName()).isEqualTo("테스터");
        // Repository의 해당 메서드가 정확히 한 번 호출되었는지 검증
        verify(eventParticipationQueryRepository).findParticipantsByEventId(
                eq(1L), anyString(), eq(pageable));
    }

    @Test
    void 이벤트별_참가자_없을경우_빈페이지_리턴() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "regDate"));
        given(eventParticipationQueryRepository.findParticipantsByEventId(eq(1L), anyString(), eq(pageable)))
                .willReturn(Page.empty());

        Page<EventParticipantResponseDto> result = eventParticipationService.getParticipantsByEvent(1L,"테스터",0,10);

        assertThat(result.getContent()).isEmpty();
    }


    @Test
    void getTodayCheckInEventIds() {
    }
}