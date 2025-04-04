package com.example.evently.point.service;

import com.example.evently.point.domain.PointHistory;
import com.example.evently.point.dto.PointHistoryResponseDto;
import com.example.evently.point.repository.PointHistoryRepository;
import com.example.evently.user.domain.User;
import com.example.evently.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;

    /**
     * 포인트 적립 메서드
     * - 사용자가 이벤트에 참여하면 포인트가 증가하고, 증가한 내역이 저장됨
     * - 사용자 총 포인트를 업데이트 하고 Redis 캐시를 갱신
     * @param user
     * @param points
     * @param reason
     */
    @CacheEvict(value = "userPoints", key = "'user:points:' + #user.id") // 포인트 적립 시 캐싱된 데이터 삭제
    @Transactional
    public void earnPoints (User user, int points, String reason) {

        //1. 포인트 적립 내역 저장
        PointHistory pointHistory = PointHistory.earnPoints(user, points, reason);
        pointHistoryRepository.save(pointHistory);

        //2. 사용자 포인트 업데이트
        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.updatePoints(user.getPoints() + points);

    }

    /**
     * 사용자의 현재 포인트 조회
     * - redis 캐싱 값이 있으면 값 반환
     * - 그렇지 않은 경우 조회후 redis 에 저장
     * @param userSn
     * @return
     */
    @Cacheable(
            value = "userPoints",
            key = "'user:points:' + #userSn",
            unless = "#result == 0" // 결과가 0이면 캐싱 안 함
    )
    public int getUserPoints(Long userSn){

        // redis에 데이터가 없으면 db 조회
        User user = userRepository.findById(userSn).orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return user.getPoints();
    }

    /**
     * 사용자 포인트 내역 조회 API
     */
    @Cacheable(
            value = "userPoints",  // 캐시 공간 이름
            key = "'user:points:' + #userSn",  // 저장될 키 (동적으로 생성됨)
            unless = "#result == null"  // 결과가 null이면 캐시하지 않음
    )
    @Transactional
    public Page<PointHistoryResponseDto> getPointHistory(Long userSn, Pageable pageable){
        // 사용자 조회
        User user = userRepository.findById(userSn).orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 포인트 내역 조회(페이징)
        Page<PointHistory> pointHistories = pointHistoryRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return pointHistories.map(PointHistoryResponseDto::fromEntity);

    }

}
