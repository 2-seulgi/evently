package com.example.evently.participation.domain;

import com.example.evently.event.domain.Event;
import com.example.evently.global.entity.BaseEntity;
import com.example.evently.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "event_participation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventParticipation extends BaseEntity { // 이벤트 참여 기록

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_sn", nullable = false)
    private User user; // 참여한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id" , nullable = false)
    private Event event; // 참여한 이벤트

    @Builder
    public EventParticipation(User user, Event event){
        this.user = user;
        this.event = event;
    }

}
