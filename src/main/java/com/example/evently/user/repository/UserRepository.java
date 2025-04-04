package com.example.evently.user.repository;

import com.example.evently.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> , QuerydslPredicateExecutor<User> {
    boolean existsByUserId(String userId);

    Optional<User> findByUserId(String userId);

}
