package com.imchobo.sayren_back.domain.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimeRangeEntity {

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime regDate;

  @Column
  private LocalDateTime voidDate;

  // 즉시 만료
  public void expire() {
    this.voidDate = LocalDateTime.now();
  }


  // 만료시간 지정
  public void expireAt(LocalDateTime expireTime) {
    this.voidDate = expireTime;
  }

  // 만료되었는지 검증
  public boolean isExpired() {
    return voidDate != null && voidDate.isBefore(LocalDateTime.now());
  }
}
