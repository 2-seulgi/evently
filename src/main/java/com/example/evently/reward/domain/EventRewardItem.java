package com.example.evently.reward.domain;

import com.example.evently.event.domain.Event;
import com.example.evently.reward.domain.enums.RewardItemType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "event_reward_item")
public class EventRewardItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관된 이벤트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "reward_name", nullable = false, length = 100)
    private String rewardName;

    @Column(nullable = false)
    private int quantity;

    @Column
    private Float probability; // null 허용 → 확률형이 아닌 경우도 있음

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false, length = 20)
    private RewardItemType rewardType;

    public static EventRewardItem of(Event event, String rewardName, int quantity, Float probability, RewardItemType rewardType) {
        EventRewardItem item = new EventRewardItem();
        item.event = event;
        item.rewardName = rewardName;
        item.quantity = quantity;
        item.probability = probability;
        item.rewardType = rewardType;
        return item;
    }

    public boolean isAvailable() {
        return quantity > 0;
    }

    public void decreaseQuantity() {
        if(quantity <= 0){
            throw new IllegalStateException("보상 수량이 부족합니다.");
        }
        this.quantity--;
    }

}
