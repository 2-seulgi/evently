package com.example.evently.participation.controller;

import com.example.evently.participation.dto.EventParticipantResponseDto;
import com.example.evently.participation.dto.EventParticipationRequestDto;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.participation.service.EventParticipationService;
import com.example.evently.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/event-participation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    /**
     * 사용자 > 이벤트 참여
     * @param eventParticipationRequestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<String> participateEvent(@RequestBody EventParticipationRequestDto eventParticipationRequestDto) {
        eventParticipationService.participateEvent(eventParticipationRequestDto);
        return ResponseEntity.ok("이벤트 참여 완료");
    }

    /**
     * 사용자별 이벤트 참여내역 조회
     * @param userSn
     * @param eventName
     * @param startDate
     * @param endDate
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/user/{userSn}")
    public ResponseEntity<Page<EventParticipationResponseDto>> getUserParticipationHistory(
            @PathVariable Long userSn,
            @RequestParam(required = false) String eventName,  // 이벤트명 필터 (선택적)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, // 시작 날짜 필터
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate, // 종료 날짜 필터
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<EventParticipationResponseDto> history = eventParticipationService.getUserParticipationHistory(userSn, eventName, startDate, endDate, page, size);
        return ResponseEntity.ok(history);
    }

    /**
     * 관리자> 이벤트별 이벤트 참가자 조회
     * @param eventId
     * @param userName
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Page<EventParticipantResponseDto>> getParticipantsByEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) String userName,  // 사용자 이름 필터
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<EventParticipantResponseDto> participants = eventParticipationService.getParticipantsByEvent(eventId, userName,  page, size);
        return ResponseEntity.ok(participants);
    }

}
