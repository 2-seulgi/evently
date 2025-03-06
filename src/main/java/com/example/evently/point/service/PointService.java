package com.example.evently.point.service;

import com.example.evently.point.domain.PointHistory;
import com.example.evently.point.repository.PointHistoryRepository;
import com.example.evently.user.domain.User;
import com.example.evently.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.updatePoints(user.getPoints() + points);

        // 3. redis 캐싱 업데이트로 조회 속도 향상
        String key = POINT_CACHE_KEY + user.getId();
        redisTemplate.opsForValue().set(key, user.getPoints(), Duration.ofMinutes(5)); //이 데이터는 5분 후 자동 삭제
    }

    @Cacheable(key = "'user:points:' + #userSn", unless = "#result == 0")
    public int getUserPoints(Long userSn){
        String key = POINT_CACHE_KEY + userSn;
        Integer cachedPoints  = (Integer) redisTemplate.opsForValue().get(key);
        if (cachedPoints != null) {
            return cachedPoints;
        }

        // redis에 데이터가 없으면 db 조회
        User user = userRepository.findById(userSn).orElseThrow(()-> new IllegalStateException("사용자를 찾을 수 없습니다."));
        int points = user.getPoints();

        // redis에 저장
        redisTemplate.opsForValue().set(key, points, Duration.ofMinutes(5));

        return points;
    }

}
