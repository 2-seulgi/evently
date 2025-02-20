package com.example.evently.event.controller;

import com.example.evently.event.dto.EventRequestDto;
import com.example.evently.event.dto.EventResponseDto;
import com.example.evently.event.service.EventService;
import com.example.evently.global.dto.SuccessResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    // 이벤트 등록
    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent (@Valid @RequestBody EventRequestDto requestDto) {
        EventResponseDto responseDto = eventService.createEvent(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 이벤트 리스트 조회
    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEvents());
    }

    // 검색 조건에 따른 이벤트 리스트 조회
    @GetMapping("/search")
    public ResponseEntity<List<EventResponseDto>> searchEvents(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) Integer minPoint
    ) {
        List<EventResponseDto> events = eventService.searchEvents(title, startDate, endDate, minPoint);
        return ResponseEntity.ok(events);

    }

    // 이벤트 상세
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventById(id));
    }

    // 이벤트 수정
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id, @RequestBody EventRequestDto eventRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEvent(id, eventRequestDto));
    }

    // 이벤트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponseDto> softDeleteEvent(@PathVariable Long id) {
        try {
            eventService.softDeleteEvent(id);
            return ResponseEntity.ok(SuccessResponseDto.of("이벤트가 성공적으로 삭제되었습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(SuccessResponseDto.of(e.getMessage()));
        }
    }


}
