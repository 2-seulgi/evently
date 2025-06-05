package com.example.evently.user.repository;

import com.example.evently.user.domain.QUser;
import com.example.evently.user.domain.enums.UserRole;
import com.example.evently.user.domain.enums.UserStatus;
import com.example.evently.user.dto.UserResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom  {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<UserResponseDto> findAllByAdminFilters(
            String userId,
            String userName,
            String userRole,
            String isUse,
            String userStatus,
            Pageable pageable
    ) {
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        if(userId != null && !userId.isEmpty()) {
            builder.and(user.userId.containsIgnoreCase(userId));
        }

        if(userName != null && !userName.isEmpty()) {
            builder.and(user.userName.containsIgnoreCase(userName));
        }

        if(userRole != null && !userRole.isEmpty()) {
            try{
                UserRole roleEnum = UserRole.valueOf(userRole.toUpperCase());
                builder.and(user.userRole.eq(roleEnum));
            } catch (IllegalArgumentException ignored){
                log.warn("[UserRepository] 잘못된 userRole 값: {}", userRole);
            }
        }

        if(userStatus != null && !userStatus.isEmpty()) {
            try {
                UserStatus statusEnum = UserStatus.valueOf(userStatus.toUpperCase());
                builder.and(user.userStatus.eq(statusEnum));
            } catch (IllegalArgumentException ignored){
                log.warn("[UserRepository] 잘못된 userStatus 값: {}", userStatus);
                }
            }

        if (isUse != null && !isUse.isBlank()) {
            if (isUse.equalsIgnoreCase("true") || isUse.equalsIgnoreCase("false")) {
                builder.and(user.isUse.eq(Boolean.parseBoolean(isUse)));
            }
        }

        List<UserResponseDto> results = jpaQueryFactory
                .select(Projections.constructor(UserResponseDto.class,
                        user.id,                // ① Long id
                        user.userId,            // ② String userId
                        user.userName,          // ③ String userName
                        user.points,            // ④ int points
                        user.isUse,             // ⑤ boolean isUse
                        user.userRole,          // ⑥ UserRole
                        user.userStatus,        // ⑦ UserStatus
                        user.regDate,           // ⑧ LocalDateTime
                        user.chgDate            // ⑨ LocalDateTime
                ))
                .from(user)
                .where(builder)
                .orderBy(user.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne()).orElse(0L);
        return new PageImpl<>(results, pageable, total);
    }
}

