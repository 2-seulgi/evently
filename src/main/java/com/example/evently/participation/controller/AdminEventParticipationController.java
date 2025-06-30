package com.example.evently.participation.controller;

import com.example.evently.participation.dto.EventParticipantResponseDto;
import com.example.evently.participation.service.EventParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/event-participation")
@RequiredArgsConstructor
@Tag(name="관리자 이벤트 참가 조회 API ",description = "관리자에서 이벤트 참여자를 보기 위한 API 입니다 ")
@PreAuthorize("hasRole('ADMIN')") // 관리자가 아닐 경우 차단
public class AdminEventParticipationController {
    private final EventParticipationService eventParticipationService;
    /**
     * 관리자> 이벤트별 이벤트 참가자 조회
     * @param eventSn
     * @param userName
     * @param page
     * @param size
     * @return
     */
    @Operation(summary="이벤트별 이벤트 참가자 조회", description = "이벤트 참가자 목록을 페이징 형태로 조회합니다. ")
    @GetMapping("/event/{eventSn}")
    public ResponseEntity<Page<EventParticipantResponseDto>> getParticipantsByEvent(
            @Parameter(description = "이벤트 식별자(PK)", example = "1")
            @PathVariable Long eventSn,

            @Parameter(description = "이벤트 참가자 이름", example = "홍길동")
            @RequestParam(required = false) String userName,  // 사용자 이름 필터

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Page<EventParticipantResponseDto> participants = eventParticipationService.getParticipantsByEvent(eventSn, userName,  page, size);
        return ResponseEntity.ok(participants);
    }
}
