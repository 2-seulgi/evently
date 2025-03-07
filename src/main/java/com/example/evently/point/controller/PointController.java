package com.example.evently.point.controller;

import com.example.evently.point.dto.PointHistoryResponseDto;
import com.example.evently.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    /**
     * 사용자의 현재 포인트 조회
     * @param userSn
     * @return
     */
    @GetMapping("/{userSn}/points")
    public ResponseEntity<Integer> getUserPoints(@PathVariable Long userSn) {
        int points = pointService.getUserPoints(userSn);
        return ResponseEntity.ok(points);
    }

    /**
     * 사용자 포인트 내역 조회 API
     */
    @GetMapping("/{userSn}")
    public ResponseEntity<Page<PointHistoryResponseDto>> getPointHistory(
            @PathVariable Long userSn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Pageable pageable = PageRequest.of(page -1, size, Sort.by(Sort.Direction.DESC," createdAt"));
        Page<PointHistoryResponseDto> points = pointService.getPointHistory(userSn, pageable);

        return ResponseEntity.ok(points);

    }



}
