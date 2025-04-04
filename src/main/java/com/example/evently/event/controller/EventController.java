package com.example.evently.event.controller;

import com.example.evently.auth.CustomUserDetails;
import com.example.evently.event.dto.EventResponseDto;
import com.example.evently.event.service.EventService;
import com.example.evently.participation.service.EventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventParticipationService eventParticipationService;

    /**
     * 이벤트 리스트 조회
     * @param title
     * @param startDate
     * @param endDate
     * @param minPoint
     * @param page
     * @param size
     * @param sortBy
     * @param direction
     * @return
     */
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
        Pageable pageable = PageRequest.of(page -1 ,size, Sort.by(
                direction.equalsIgnoreCase("desc")? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        ));
        return ResponseEntity.ok(eventService.getEvents(title, startDate, endDate, minPoint, pageable));
    }

    /**
     * 이벤트 상세
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventById(id));
    }

    /**
     * 사용자 > 이벤트 참여
     * @param
     * @return
     */
    @PostMapping("/{eventId}/participation")
    public ResponseEntity<?> participateInEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        int pointReward = eventParticipationService.participate(eventId, userDetails.getUser().getId());

        return ResponseEntity.ok(Map.of(
                "msgCd", "Success",
                "pointReward", pointReward
        ));
    }
}
