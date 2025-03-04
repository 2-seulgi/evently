package com.example.evently.participation.service;

import com.example.evently.event.domain.Event;
import com.example.evently.event.repository.EventRepository;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.participation.dto.EventParticipantResponseDto;
import com.example.evently.participation.dto.EventParticipationRequestDto;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.participation.repository.EventParticipationQueryRepository;
import com.example.evently.participation.repository.EventParticipationRepository;
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

@Service
@RequiredArgsConstructor
public class EeventParticipationService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventParticipationQueryRepository eventParticipationQueryRepository;


    /**
     * 이벤트 참가
     * @param eventParticipationRequestDto
     */
    @Transactional
    public void participateEvent(EventParticipationRequestDto  eventParticipationRequestDto) {
        // 사용자 조회
        User user = userRepository.findById(eventParticipationRequestDto.userSn())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        // 이벤트 조회
        Event event = eventRepository.findById(eventParticipationRequestDto.eventId())
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트를 찾을 수 없습니다."));

        // 중복 참여 여부 확인
        boolean alreadyParticipated = eventParticipationRepository.existsByUserAndEvent(user, event);
        if (alreadyParticipated) {
            throw new IllegalStateException("이미 참여한 이벤트입니다.");
        }

        // 이벤트 참여 저장
        EventParticipation eventParticipation = EventParticipation.builder()
                .user(user)
                .event(event)
                .build();
        eventParticipationRepository.save(eventParticipation);

        // 포인트 지급
        user.addPoints(event.getPointReward());
        userRepository.save(user); // 포인트 저장
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
