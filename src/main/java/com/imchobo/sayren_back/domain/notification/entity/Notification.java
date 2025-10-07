package com.imchobo.sayren_back.domain.notification.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.notification.en.NotificationStatus;
import com.imchobo.sayren_back.domain.notification.en.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
  @Column(name = "notification_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationType type;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String message;

  // 추가  알림이 연결된 엔티티 ID (예: 구독ID, 회차ID 등)
  @Column(name = "target_id")
  private Long targetId;


  private String linkUrl;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private NotificationStatus status = NotificationStatus.UNREAD;
}
