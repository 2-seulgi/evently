package com.example.evently.point.controller;

import com.example.evently.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/{userSn}/points")
    public ResponseEntity<Integer> getUserPoints(@PathVariable Long userSn) {
        int points = pointService.getUserPoints(userSn);
        return ResponseEntity.ok(points);
    }


}
