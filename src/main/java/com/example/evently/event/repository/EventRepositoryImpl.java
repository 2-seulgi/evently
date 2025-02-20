package com.example.evently.event.repository;

import com.example.evently.event.domain.Event;
import com.example.evently.event.domain.QEvent;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventRepositoryImpl implements EventRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QEvent event = QEvent.event;

    public EventRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<Event> findAll(BooleanBuilder builder) {
        return queryFactory
                .selectFrom(event)
                .where(builder)
                .fetch();
    }
}
