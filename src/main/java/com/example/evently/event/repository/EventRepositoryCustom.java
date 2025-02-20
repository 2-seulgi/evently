package com.example.evently.event.repository;
import com.example.evently.event.domain.Event;
import com.querydsl.core.BooleanBuilder;

import java.util.List;

public interface EventRepositoryCustom {
    List<Event> findAll(BooleanBuilder builder);
}
