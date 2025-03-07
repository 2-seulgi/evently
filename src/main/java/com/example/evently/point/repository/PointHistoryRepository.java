package com.example.evently.point.repository;

import com.example.evently.point.domain.PointHistory;
import com.example.evently.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PointHistoryRepository extends JpaRepository<PointHistory , Long>, QuerydslPredicateExecutor<PointHistory> {
    // 특정 사용자의 포인트 내역을 최신순으로 조회
    Page<PointHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
