package com.example.evently.reward.controller;


import com.example.evently.reward.service.draw.DrawRewardProcessor;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/draw")
@Tag(name="관리자 랜덤 추첨 API", description = "관리자 권한이 있는 사람만 경품 추첨이벤트에서 추첨 이벤트를 실행한다.")
public class AdminDrawRewardController {
    private final DrawRewardProcessor drawRewardProcessor;

    // 🔐 관리자 권한 필요 (권한 설정 시 적용)
    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN')") // 관리자가 아닐 경우 차단
    public ResponseEntity<String> processDrawRewardsManually() {
        drawRewardProcessor.processDrawRewards();
        return ResponseEntity.ok("랜덤 추첨 보상 처리가 완료되었습니다.");
    }

}
