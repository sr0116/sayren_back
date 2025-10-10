package com.imchobo.sayren_back.domain.notification.dto;

import lombok.Data;

import java.util.List;

@Data
public class NotificationBatchRequestDTO {

  // 선택 알람 삭제관련 DTO
  private List<Long> notificationIds;
}
