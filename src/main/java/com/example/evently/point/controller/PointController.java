package com.example.evently.point.controller;

import com.example.evently.auth.CustomUserDetails;
import com.example.evently.point.dto.PointHistoryResponseDto;
import com.example.evently.point.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자의 적립된 포인트와, 포인트 내역을 조회하는 API ")
public class PointController {

    private final PointService pointService;

    /**
     * 로그인한 사용자의 userSn(ID)를 기반으로 본인 포인트를 조회
     * @param userDetails
     * @return
     */
    @Operation(summary = "로그인한 사용자의 본인 포인트를 조회하는 API", description = "로그인한 사용자의 userSn(ID)를 기반으로 본인 포인트를 조회합니다." )
    @GetMapping("/points")
    public ResponseEntity<?> getUserPoints(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userSn = userDetails.getUser().getId(); // 로그인한 사용자의 PK
        int points = pointService.getUserPoints(userSn);
        return ResponseEntity.ok(Map.of("points", points)); // 키가 포함된 객체 응답
    }

    /**
     * 사용자 포인트 내역 조회 API
     */
    @Operation(summary = "적립된 포인트 히스토리를 조회하는 API", description = "로그인한 사용자가 적립한 포인트 내역을 페이징 형태로 조회힙니다." )
    @GetMapping("/{userSn}")
    public ResponseEntity<Page<PointHistoryResponseDto>> getPointHistory(
            @PathVariable Long userSn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Pageable pageable = PageRequest.of(page , size, Sort.by(Sort.Direction.DESC," createdAt"));
        Page<PointHistoryResponseDto> points = pointService.getPointHistory(userSn, pageable);

        return ResponseEntity.ok(points);

    }



}
