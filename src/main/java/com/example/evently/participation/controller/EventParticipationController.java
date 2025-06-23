package com.example.evently.participation.controller;

import com.example.evently.auth.CustomUserDetails;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.participation.service.EventParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "이벤트 참여 및 참여 조회 API",description = "사용자 이벤트 참여 및 참여 내역을 보기 위한 API 입니다.")
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    /**
     * 마이페이지 > 사용자별 이벤트 참여내역 조회
     * @param userDetails
     * @param eventTitle
     * @param startDate
     * @param endDate
     * @param page
     * @param size
     * @return
     */
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "사용자 참여 내역 조회", description = "로그인한 사용자의 이벤트 참여 이력을 페이징 형태로 조회합니다. " +
            "이벤트 제목, 기간별 검색이 가능합니다.")
    @GetMapping("/me/participation")
    public ResponseEntity<Page<EventParticipationResponseDto>> getMyParticipationHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String eventTitle,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = userDetails.getUser().getId();
        Page<EventParticipationResponseDto> history = eventParticipationService.getUserParticipationHistory(
                userId, eventTitle, startDate, endDate, page, size
        );
        return ResponseEntity.ok(history);
    }

    /**
     * 사용자 > 이벤트 참여
     * @param
     * @return
     */
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "이벤트 참여", description = "사용자가 특정 이벤트에 참여 합니다." +
    "참여 성공 시 포인트가 지급되며, 중복 참여는 방지됩니다. ")
    @PostMapping("/me/participation/{eventSn}")
    public ResponseEntity<?> participateInEvent(
            @PathVariable Long eventSn,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        int pointReward = eventParticipationService.participateInEvent(eventSn, userDetails.getUser().getId());

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
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "오늘 출석 이벤트 참여 여부 조회",
            description = "오늘 날짜 기준으로 사용자가 이미 참여한 출석체크 이벤트 ID 목록을 반환합니다."
    )
    @GetMapping("/me/participation/checkin/today")
    public ResponseEntity<List<Long>> getTodayCheckinParticipation(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<Long> attendedToday = eventParticipationService.getTodayCheckInEventIds(userDetails.getUser().getId());
        return ResponseEntity.ok(attendedToday); // 오늘 참여한 출석 이벤트 ID 리스트
    }

}
