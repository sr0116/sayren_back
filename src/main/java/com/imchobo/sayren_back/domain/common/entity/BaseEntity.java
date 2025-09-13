package com.imchobo.sayren_back.domain.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass                                       // 테이블 직접 생성X, 하위 클래스에 칼럼 상속
@EntityListeners(AuditingEntityListener.class)          // 생성/수정 시간 자동 세팅
public abstract class BaseEntity {

    // DB 컬럼명이 regdate 라서 name으로 매핑
    @CreatedDate
    @Column(name = "regdate", updatable = false)          // 최초 생성 시 한 번만 기록
    private LocalDateTime regDate;

    // DB 컬럼명이 moddate 라서 name으로 매핑
    @LastModifiedDate
    @Column(name = "moddate")                             // 변경 시마다 자동 갱신
    private LocalDateTime modDate;
}
