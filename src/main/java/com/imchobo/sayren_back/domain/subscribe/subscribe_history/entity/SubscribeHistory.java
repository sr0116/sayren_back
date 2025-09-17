package com.imchobo.sayren_back.domain.subscribe.subscribe_history.entity;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_subscribe_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeHistory extends CreatedEntity {

  // 구독 히스토리 PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "subscribe_history_id")
  private Long id;

  // 구독 FK (필수)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "subscribe_id", nullable = false)
  private Subscribe subscribe;

  // 구독 상태 (Enum)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SubscribeStatus status;

  // 상태 변경 사유 (상세 설명)
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ReasonCode reasonCode;

  // 변경자
  @Column(nullable = false)
  private ActorType changedBy;

  // 생성일시는 CreatedEntity에서 상속
}
