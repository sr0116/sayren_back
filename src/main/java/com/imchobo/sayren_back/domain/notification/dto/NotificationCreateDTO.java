package com.imchobo.sayren_back.domain.notification.dto;

import com.imchobo.sayren_back.domain.notification.en.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
public class NotificationCreateDTO {
  private Long memberId; // 알림에서만
  private NotificationType type;
  private String title;
  private String message;
  private Long targetId;
  private String linkUrl;
}
