package com.example.evently.participation.controller;

import com.example.evently.participation.dto.EventParticipantResponseDto;
import com.example.evently.participation.service.EventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/event-participation")
@RequiredArgsConstructor
public class AdminEventParticipationController {
    private final EventParticipationService eventParticipationService;
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
