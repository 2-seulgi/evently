package com.example.evently.participation.controller;

import com.example.evently.auth.CustomUserDetails;
import com.example.evently.participation.dto.EventParticipantResponseDto;
import com.example.evently.participation.dto.EventParticipationRequestDto;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.participation.service.EventParticipationService;
import com.example.evently.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    /**
     * 마이페이지 > 사용자별 이벤트 참여내역 조회
     * @param userDetails
     * @param eventName
     * @param startDate
     * @param endDate
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/me/participation")
    public ResponseEntity<Page<EventParticipationResponseDto>> getMyParticipationHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userSn = userDetails.getUser().getId();
        Page<EventParticipationResponseDto> history = eventParticipationService.getUserParticipationHistory(
                userSn, eventName, startDate, endDate, page, size
        );
        return ResponseEntity.ok(history);
    }

    /**
     * 사용자 > 이벤트 참여
     * @param
     * @return
     */
    @PostMapping("/me/participation/{eventId}")
    public ResponseEntity<?> participateInEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        int pointReward = eventParticipationService.participateInEvent(eventId, userDetails.getUser().getId());

        return ResponseEntity.ok(Map.of(
                "msgCd", "Success",
                "pointReward", pointReward
        ));
    }

    /**
     *
     * 오늘 기준 출석체크 확인 버튼
     * @param userDetails
     * @return
     */
    @GetMapping("/me/participation/checkin/today")
    public ResponseEntity<List<Long>> getTodayCheckinParticipation(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<Long> attendedToday = eventParticipationService.getTodayCheckInEventIds(userDetails.getUser().getId());
        return ResponseEntity.ok(attendedToday); // 오늘 참여한 출석 이벤트 ID 리스트
    }

}
