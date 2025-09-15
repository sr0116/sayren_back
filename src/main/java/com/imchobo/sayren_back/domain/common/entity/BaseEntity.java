package com.imchobo.sayren_back.domain.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {


  // DB 컬럼명이 regdate 라서 매핑 지정
  @CreatedDate
  @Column(name = "regdate", updatable = false)
  private LocalDateTime regDate;


  // DB 컬럼명이 moddate 라서 매핑 지정

  @LastModifiedDate
  @Column(name = "moddate")
  private LocalDateTime modDate;
}
