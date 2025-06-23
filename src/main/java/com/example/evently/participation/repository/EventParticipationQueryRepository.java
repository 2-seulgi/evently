package com.example.evently.participation.repository;

import com.example.evently.event.domain.QEvent;
import com.example.evently.participation.domain.QEventParticipation;
import com.example.evently.participation.dto.EventParticipantResponseDto;
import com.example.evently.participation.dto.EventParticipationResponseDto;
import com.example.evently.user.domain.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventParticipationQueryRepository {
    private final JPAQueryFactory queryFactory;
    public Page<EventParticipationResponseDto> findUserParticipationHistory(Long userId, String eventName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QEventParticipation eventParticipation = QEventParticipation.eventParticipation;
        QEvent  event = QEvent.event;
        QUser user = QUser.user; // 유저 엔티티 추가

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(eventParticipation.user.id.eq(userId)); // 기본적으로 사용자 ID 필터 적용

        // 검색 조건 추가 (이벤트명, 시작 날짜, 종료 날짜)
        if (eventName != null && !eventName.isBlank()) {
            builder.and(event.title.containsIgnoreCase(eventName)); // 이벤트명 검색 (대소문자 무시)
        }
        if (startDate != null) {
            builder.and(eventParticipation.regDate.goe(startDate)); // 지정된 날짜 이후 참여한 내역
        }
        if (endDate != null) {
            builder.and(eventParticipation.regDate.loe(endDate)); // 지정된 날짜 이전 참여한 내역
        }

        List<EventParticipationResponseDto> results = queryFactory
                .select(Projections.constructor(EventParticipationResponseDto.class,
                        eventParticipation.event.id,
                        event.title,
                        event.pointReward,
                        eventParticipation.regDate
                        ))
                .from(eventParticipation)
                .join(event).on(eventParticipation.event.eq(event))
                .join(user).on(eventParticipation.user.eq(user))  // 유저와 조인
                .where(builder) // 동적 필터 적용
                .orderBy(eventParticipation.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory
                .select(eventParticipation.count())
                .from(eventParticipation)
                .where(builder)
                .fetchOne()).orElse(0L); // NullPointerException 방지

        return new PageImpl<>(results, pageable, total);
    }

    public Page<EventParticipantResponseDto> findParticipantsByEventId(Long eventId, String userName, Pageable pageable) {
        QEventParticipation eventParticipation = QEventParticipation.eventParticipation;
        QUser user = QUser.user; // 유저 엔티티 추가

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(eventParticipation.event.id.eq(eventId));// 특정 이벤트에 대한 참여자 조회

        // 검색조건
        if (userName != null && !userName.isBlank()) {
            builder.and(user.userName.containsIgnoreCase(userName));
        }

        // Query 실행
        List<EventParticipantResponseDto> results = queryFactory
                .select(Projections.constructor(EventParticipantResponseDto.class,
                        user.id,
                        user.userName,
                        eventParticipation.regDate
                        ))
                .from(eventParticipation)
                .join(user).on(eventParticipation.user.eq(user))
                .where(builder)
                .orderBy(eventParticipation.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory
                .select(eventParticipation.count())
                .from(eventParticipation)
                .where(builder)
                .fetchOne()).orElse(0L); // NullPointerException 방지

        return new PageImpl<>(results, pageable, total);

    }
}
