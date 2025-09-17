package com.imchobo.sayren_back.domain.subscribe.subscribe_history.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.en.SubscribeHistoryStatus;
import jakarta.persistence.*;
import lombok.*;

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
  private Long subscribeHistoryId;

  // 구독 FK (필수)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "subscribe_id", nullable = false)
  private Subscribe subscribe;

  // 구독 상태 (Enum)
  // NOT NULL, VARCHAR(20)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SubscribeHistoryStatus status;

  // 상태 변경 사유 (상세 설명)
  @Column(columnDefinition = "TEXT")
  private String reason;

  // 변경자 (SYSTEM)
  @Column(name = "changed_by")
  private String changedBy = "SYSTEM";

  // 생성일시는 CreatedEntity에서 상속
}
