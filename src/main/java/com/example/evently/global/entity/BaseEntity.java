package com.example.evently.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass //  다른 엔티티가 상속받을 때 공통 필드 포함
@EntityListeners(AuditingEntityListener.class) // JPA에서 자동으로 날짜 관리
public abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime regDate; //  등록일 (생성 시 자동 저장)

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime chgDate; //  수정일 (업데이트 시 자동 변경)

    @Column(nullable = false)
    private boolean isDeleted = false; //  삭제 여부 (Soft Delete)

    // Soft Delete 처리 메소드 추가
    public void softDelete() {
        this.isDeleted = true;
    }
}