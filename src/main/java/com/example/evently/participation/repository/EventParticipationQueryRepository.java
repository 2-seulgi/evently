package com.example.evently.participation.repository;

import com.example.evently.event.domain.QEvent;
import com.example.evently.participation.domain.QEventParticipation;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.user.domain.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class EventParticipationQueryRepository {
    private final JPAQueryFactory queryFactory;
    public Page<EventParticipationResponseDto> findUserParticipationHistory(Long userSn, Pageable pageable) {
        QEventParticipation eventParticipation = QEventParticipation.eventParticipation;
        QEvent  event = QEvent.event;
        QUser user = QUser.user; // 유저 엔티티 추가

        List<EventParticipationResponseDto> results = queryFactory
                .select(Projections.constructor(EventParticipationResponseDto.class,
                        eventParticipation.event.id,
                        event.event.title,
                        eventParticipation.regDate
                        ))
                .from(eventParticipation)
                .join(event).on(eventParticipation.event.eq(event))
                .join(user).on(eventParticipation.user.eq(user))  // 유저와 조인
                .where(eventParticipation.user.id.eq(userSn)) //  user.id 로 조회
                .orderBy(eventParticipation.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Objects.requireNonNullElse(
                queryFactory
                        .select(eventParticipation.count())
                        .from(eventParticipation)
                        .where(eventParticipation.user.id.eq(userSn))
                        .fetchOne(),
        0L
        );
        return new PageImpl<>(results, pageable, total);
    }
}
