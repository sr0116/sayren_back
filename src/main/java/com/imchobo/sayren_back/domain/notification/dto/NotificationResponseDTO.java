package com.imchobo.sayren_back.domain.notification.dto;

import com.imchobo.sayren_back.domain.notification.en.NotificationStatus;
import com.imchobo.sayren_back.domain.notification.en.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {

  private Long notificationId;
  private NotificationType type;
  private String title;
  private String message;
  private String linkUrl;
  private Long targetId;
  private NotificationStatus status;
  private LocalDateTime createdAt;

}
