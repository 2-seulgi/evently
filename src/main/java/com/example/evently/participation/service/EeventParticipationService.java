package com.example.evently.participation.service;

import com.example.evently.event.domain.Event;
import com.example.evently.event.repository.EventRepository;
import com.example.evently.participation.domain.EventParticipation;
import com.example.evently.participation.dto.EventParticipationRequestDto;
import com.example.evently.participation.repository.EventParticipationRepository;
import com.example.evently.user.domain.User;
import com.example.evently.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EeventParticipationService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventParticipationRepository eventParticipationRepository;

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

}
