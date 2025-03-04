package com.example.evently.participation.contorller;

import com.example.evently.participation.dto.EventParticipationRequestDto;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.participation.service.EeventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event-participation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EeventParticipationService eventParticipationService;

    @PostMapping
    public ResponseEntity<String> participateEvent(@RequestBody EventParticipationRequestDto eventParticipationRequestDto) {
        eventParticipationService.participateEvent(eventParticipationRequestDto);
        return ResponseEntity.ok("이벤트 참여 완료");
    }

    @GetMapping("/user/{userSn}")
    public ResponseEntity<Page<EventParticipationResponseDto>> getUserParticipationHistory(
            @PathVariable Long userSn,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<EventParticipationResponseDto> history = eventParticipationService.getUserParticipationHistory(userSn, page, size);
        return ResponseEntity.ok(history);
    }

}
