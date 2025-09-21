package com.imchobo.sayren_back.domain.subscribe.entity;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_subscribe_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
  @Builder.Default
  private SubscribeStatus status=SubscribeStatus.PENDING_PAYMENT;

  // 상태 변경 사유 (상세 설명)
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ReasonCode reasonCode;

  // 변경자
  @Column(nullable = false)
  @Builder.Default
  private ActorType changedBy=ActorType.SYSTEM; // 이거 나중에 확인하기 기본값 세팅
  // 생성일시는 CreatedEntity에서 상속
}
