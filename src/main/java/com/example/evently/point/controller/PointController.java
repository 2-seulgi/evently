package com.example.evently.point.controller;

import com.example.evently.auth.CustomUserDetails;
import com.example.evently.point.dto.PointHistoryResponseDto;
import com.example.evently.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    /**
     * 로그인한 사용자의 userSn(ID)를 기반으로 본인 포인트를 조회
     * @param userDetails
     * @return
     */
    @GetMapping("/points")
    public ResponseEntity<Integer> getUserPoints(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userSn = userDetails.getUser().getId(); // 로그인한 사용자의 PK
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
