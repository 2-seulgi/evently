package com.example.evently.event.controller;

import com.example.evently.event.dto.EventResponseDto;
import com.example.evently.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "사용자 이벤트 API", description = "사용자 이벤트 조회 관련 API 입니다 ")
public class EventController {
    private final EventService eventService;

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
    @Operation(summary = "이벤트 리스트 조회", description = "이벤트 목록을 조회힙니다. ")
    @GetMapping
    public ResponseEntity<Page<EventResponseDto>> getEvents(
            @Parameter(description = "이벤트 제목", example = "출석체크 이벤트")
            @RequestParam(required = false) String title,

            @Parameter(description = "이벤트 시작일", example = "2025-05-18 07:24:48")
            @RequestParam(required = false) LocalDateTime startDate,

            @Parameter(description = "이벤트 종료일", example = "2025-06-18 07:24:48")
            @RequestParam(required = false) LocalDateTime endDate,

            @Parameter(description = "받는 포인트", example = "2")
            @RequestParam(required = false) Integer minPoint,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "정렬 기준 필드", example = "id")
            @RequestParam(defaultValue = "id") String sortBy, // 기본값: 등록일(=id 기준)

            @Parameter(description = "정렬 방향", example = "desc")
            @RequestParam(defaultValue = "desc") String direction
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
        return ResponseEntity.ok(eventService.getEvents(title, startDate, endDate, minPoint,
                false, pageable));
    }

    /**
     * 이벤트 상세
     * @param id
     * @return
     */
    @Operation(summary = "이벤트 상세 내용 조회", description = "이벤트 상세 내용을 조회힙니다. ")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventById(id));
    }

}
