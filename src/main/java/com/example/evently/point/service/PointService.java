package com.example.evently.point.service;

import com.example.evently.point.domain.PointHistory;
import com.example.evently.point.repository.PointHistoryRepository;
import com.example.evently.user.domain.User;
import com.example.evently.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    private static final String POINT_CACHE_KEY = "user:points:";

    @Transactional
    public void earnPoints (User user, int points, String reason) {

        //1. 포인트 적립 내역 저장
        PointHistory pointHistory = PointHistory.earnPoints(user, points, reason);
        pointHistoryRepository.save(pointHistory);

        //2. 사용자 포인트 업데이트
        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        user.updatePoints(user.getPoints() + points);

        // 3. redis 캐싱 업데이트로 조회 속도 향상
        String key = POINT_CACHE_KEY + user.getId();
        redisTemplate.opsForValue().set(key, user.getPoints(), Duration.ofMinutes(5)); //이 데이터는 5분 후 자동 삭제

    }
}
