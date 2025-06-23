package com.example.evently.user.repository;

import com.example.evently.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> ,UserRepositoryCustom {
    boolean existsByLoginId(String loginId);
    Optional<User> findByLoginId(String loginId);
}
