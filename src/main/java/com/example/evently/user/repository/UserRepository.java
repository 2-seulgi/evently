package com.example.evently.user.repository;

import com.example.evently.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.nio.channels.FileChannel;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> ,UserRepositoryCustom {
    boolean existsByUserId(String userId);
    Optional<User> findByUserId(String userId);
}
