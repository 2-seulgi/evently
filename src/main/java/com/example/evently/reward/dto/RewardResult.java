package com.example.evently.reward.dto;

public record  RewardResult(
        boolean isWinner, // 당첨여부
        String rewardName, // "에어팟"(없으면 null)
        String message // 메세지
) {
    public static RewardResult win(String rewardName) {
        return new RewardResult(true, rewardName, rewardName+"에 담첨되셨습니다.🎉");
    }
    public static RewardResult lose(String rewardName) {
        return new RewardResult(false, null, "아쉽게도 당첨되지 않았습니다.😔");
    }
    public static RewardResult pending() {
        return new RewardResult(false, null, "이벤트 종료 후 추첨이 진행됩니다.");
    }
}
