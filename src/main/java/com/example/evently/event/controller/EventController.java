package com.example.evently.event.controller;

import com.example.evently.event.dto.EventRequestDto;
import com.example.evently.event.dto.EventResponseDto;
import com.example.evently.event.service.EventService;
import com.example.evently.global.dto.SuccessResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    public ResponseEntity<Page<EventResponseDto>> getEvents(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) Integer minPoint,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy, // 기본값: 등록일(=id 기준)
            @RequestParam(defaultValue = "desc") String direction
    ) {
        /**
         * 페이지네이션 객체 생성
         * page: 현재 페이지 번호
         * size : 한 페이지당 데이터 개수
         * 정렬 필드와 방향을 동적으로 설정
         */
        Pageable pageable = PageRequest.of(page,size, Sort.by(
                direction.equalsIgnoreCase("desc")? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        ));
        return ResponseEntity.ok(eventService.getEvents(title, startDate, endDate, minPoint, pageable));

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
