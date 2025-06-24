package com.example.evently.reward.domain;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.enums.RewardType;
import com.example.evently.global.entity.BaseEntity;
import com.example.evently.participation.domain.enums.RewardStatus;
import com.example.evently.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="event_reward_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventRewardHistory  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 참여한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id" , nullable = false)
    private Event event; // 참여한 이벤트

    @Enumerated(EnumType.STRING)
    @Column(name ="reward_type", nullable = false, length = 20)
    private RewardType rewardType;

    @Enumerated(EnumType.STRING)
    @Column(name ="reward_status", nullable = false, length = 20)
    private RewardStatus rewardStatus;
}
