package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.common.entity.CreatedEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_subscribe_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeHistory extends CreatedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "subscribe_history_id")
  private Long subscribeHistoryId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subscribe_id", nullable = false)
  private Subscribe subscribe;

  @Column(nullable = false)
  private String status; // 변경 상태

  private String reason;

  private String changeBy; // 변경자(기본값 시스템)

  // regdate 상속 생략(나중에 주석 지우기)


}
