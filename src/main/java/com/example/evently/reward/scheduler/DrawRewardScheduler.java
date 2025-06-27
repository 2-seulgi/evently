package com.example.evently.reward.scheduler;

import com.example.evently.reward.service.draw.DrawRewardProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DrawRewardScheduler {


    private final DrawRewardProcessor drawRewardProcessor;

    // 🕒 매일 새벽 3시에 실행 (cron 형식: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 3 * * *")
    public void runDrawRewardProcessing() {
        log.info("⏰ [스케줄러] 랜덤 추첨 보상 처리 시작");
        drawRewardProcessor.processDrawRewards();
        log.info("✅ [스케줄러] 랜덤 추첨 보상 처리 완료");
    }
}