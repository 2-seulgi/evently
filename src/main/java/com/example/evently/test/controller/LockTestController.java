package com.example.evently.test.controller;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class LockTestController {
    private final RedissonClient redissonClient;

    @GetMapping("/lock-test")
    public String test() {
        RLock lock = redissonClient.getLock("reward:event:test");

        boolean isLocked = false;
        try{
            // 락 시도( 0초 대기 , 락 획득하면 5초 점유)
            isLocked = lock.tryLock(0,5, TimeUnit.SECONDS);
            if(isLocked){
                System.out.println("✅락 획득함");
                Thread.sleep(3000); // 실제 처리 시뮬레이션
                return "✅ 성공! 락 획득함";
            } else {
                return "❌ 실패! 이미 다른 요청이 락을 잡고 있음";
            }
        } catch (RedisConnectionException e) {
            return "🚨 Redis 장애! fallback 처리 실행됨";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "중단됨";
        } finally {
            if (isLocked) {
                lock.unlock();
                System.out.println("🔓 락 해제됨");
            }
        }
    }
}
