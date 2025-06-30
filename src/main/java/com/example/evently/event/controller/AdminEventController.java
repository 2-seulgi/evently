package com.example.evently.event.controller;

import com.example.evently.event.dto.EventRequestDto;
import com.example.evently.event.dto.EventResponseDto;
import com.example.evently.event.service.EventService;
import com.example.evently.global.dto.SuccessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Tag(name = "관리자 이벤트 API", description = "관리자 이벤트 생성/ 조회 / 수정 / 삭제 관련 API 입니다 ")
@PreAuthorize("hasRole('ADMIN')") // 관리자가 아닐 경우 차단
public class AdminEventController {
    private final EventService eventService;

    // 이벤트 등록
    @Operation(summary = "이벤트 생성" , description = "이벤트를 생성합니다. ")
    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent (@Valid @RequestBody EventRequestDto requestDto) {
        EventResponseDto responseDto = eventService.createEvent(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 이벤트 리스트 조회
    @Operation(summary = "이벤트 목록 조회", description = "전체 이벤트 목록을 페이징 형태로 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<EventResponseDto>> getEvents(
            @Parameter(description = "이벤트 제목", example = "출석체크 이벤트")
            @RequestParam(required = false) String title,

            @Parameter(description = "이벤트 시작일", example = "2025-05-18 07:24:48")
            @RequestParam(required = false) LocalDateTime startDate,

            @Parameter(description = "이벤트 종료일", example = "2025-06-18 07:24:48")
            @RequestParam(required = false) LocalDateTime endDate,

            @Parameter(description = "최소 포인트 필터", example = "2")
            @RequestParam(required = false) Integer minPoint,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "정렬 기준 필드", example = "id")
            @RequestParam(defaultValue = "id") String sortBy, // 기본값: 등록일(=id 기준)

            @Parameter(description = "정렬 방향", example = "desc")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "삭제된 이벤트도 포함할지 여부", example = "false")
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted
    ) {
        /**
         * 페이지네이션 객체 생성
         * page: 현재 페이지 번호
         * size : 한 페이지당 데이터 개수
         * 정렬 필드와 방향을 동적으로 설정
         */
        Pageable pageable = PageRequest.of(page ,size, Sort.by(
                direction.equalsIgnoreCase("desc")? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        ));
        return ResponseEntity.ok(eventService.getEvents(title, startDate, endDate, minPoint, includeDeleted, pageable));

    }

    // 이벤트 상세
    @Operation(summary = "이벤트 상세 조회", description = "특정 이벤트 상세 내용을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventById(id));
    }

    // 이벤트 수정
    @Operation(summary = "이벤트 수정", description = "특정 이벤트 내용을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id, @RequestBody EventRequestDto eventRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEvent(id, eventRequestDto));
    }

    // 이벤트 삭제
    @Operation(summary = "이벤트 삭제", description = "소프트 딜리트를 사용해 이벤트를 삭제합니다.")
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
