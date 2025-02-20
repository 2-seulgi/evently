package com.example.evently.event.service;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.QEvent;
import com.example.evently.event.dto.EventRequestDto;
import com.example.evently.event.dto.EventResponseDto;
import com.example.evently.event.repository.EventRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    // 이벤트 생성
    @Transactional
    public EventResponseDto createEvent(EventRequestDto requestDto) {
        Event event = Event.builder()
                .title(requestDto.title())
                .description(requestDto.description())
                .startDate(requestDto.startDate())
                .endDate(requestDto.endDate())
                .pointReward(requestDto.pointReward())
                .build();

        Event savedEvent = eventRepository.save(event);
        return EventResponseDto.fromEntity(savedEvent);
    }

    // 이벤트 목록 조회
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents(){
        return eventRepository.findAllByIsDeletedFalse().stream().map(EventResponseDto::fromEntity).collect(Collectors.toList());

    }

    // 이벤트 조건 조회
    @Transactional(readOnly = true)
    public List<EventResponseDto> searchEvents(String title, LocalDateTime startDate, LocalDateTime endDate, Integer minPoint) {
        BooleanBuilder builder = buildSearchPredicate(title, startDate, endDate, minPoint);
        
        // 삭제 여부 조건 추가
        builder.and(QEvent.event.isDeleted.eq(false));

        // 필터링된 데이터 조회
        List<Event> events = eventRepository.findAll(builder);

        // DTO 변환
        return events.stream()
                .map(EventResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     *  검색 조건을 BooleanBuilder로 변환하는 메서드 (별도 분리)
     */
    private BooleanBuilder buildSearchPredicate(String title, LocalDateTime startDate, LocalDateTime endDate, Integer minPoint) {
        QEvent event = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();

        if (title != null) {
            builder.and(event.title.containsIgnoreCase(title));
        }
        if (startDate != null) {
            builder.and(event.startDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(event.endDate.loe(endDate));
        }
        if (minPoint != null) {
            builder.and(event.pointReward.goe(minPoint));
        }

        return builder;
    }

    // 이벤트 상세 조회
    @Transactional(readOnly = true)
    public EventResponseDto getEventById(Long eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new IllegalArgumentException("해당 이벤트를 찾을 수 없습니다 ID: " + eventId));
        return EventResponseDto.fromEntity(event);
    }

    // 이벤트 수정
    @Transactional
    public EventResponseDto updateEvent(Long eventId, EventRequestDto requestDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new IllegalArgumentException("해당 이벤트를 찾을 수 없습니다 ID: " + eventId));
        event.updateEvent(
                requestDto.title(),
                requestDto.description(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.pointReward()
        );
        return EventResponseDto.fromEntity(event);
    }

    // 이벤트 삭제
    @Transactional
    public void softDeleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트를 찾을 수 없습니다. ID: " + eventId));
        if(event.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("이미 종료된 이벤트는 삭제할 수 없습니다.");
        }
        event.softDelete();
    }


}
