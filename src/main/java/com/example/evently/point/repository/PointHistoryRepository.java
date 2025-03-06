package com.example.evently.point.repository;

import com.example.evently.point.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PointHistoryRepository extends JpaRepository<PointHistory , Long>, QuerydslPredicateExecutor<PointHistory> {

}
