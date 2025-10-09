package com.imchobo.sayren_back.domain.notification.service;

import com.imchobo.sayren_back.domain.notification.dto.NotificationBatchRequestDTO;
import com.imchobo.sayren_back.domain.notification.dto.NotificationCreateDTO;
import com.imchobo.sayren_back.domain.notification.dto.NotificationResponseDTO;
import com.imchobo.sayren_back.domain.notification.en.NotificationType;

import java.util.List;

public interface NotificationService {


  // 알림 생성
  void send(NotificationCreateDTO dto);

  // 로그인 사용자 알림 전체 조회
  List<NotificationResponseDTO> getAllByCurrentMember();

  // 단일 알림 읽음 처리
  void markAsRead(Long notificationId);

  // 선택한 알림 읽음 처리
  void markSelectedAsRead(NotificationBatchRequestDTO dto);

  // 단일 조회
  NotificationResponseDTO getOne(Long notificationId);

  // 로그인 사용자의 전체 알림 읽음 처리
  void markAllAsRead();

  // 단일 알림 삭제
  void delete(Long notificationId);

  // 선택 알림 삭제
  void deleteSelected(NotificationBatchRequestDTO dto);

  // 로그인 사용자의 전체 알림 삭제
  void deleteAllByCurrentMember();
}
