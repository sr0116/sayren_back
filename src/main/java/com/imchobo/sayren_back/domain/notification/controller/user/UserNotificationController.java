package com.imchobo.sayren_back.domain.notification.controller.user;

import com.imchobo.sayren_back.domain.notification.dto.NotificationBatchRequestDTO;
import com.imchobo.sayren_back.domain.notification.dto.NotificationCreateDTO;
import com.imchobo.sayren_back.domain.notification.dto.NotificationResponseDTO;
import com.imchobo.sayren_back.domain.notification.repository.NotificationRepository;
import com.imchobo.sayren_back.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/notification")
public class UserNotificationController {
  private final NotificationService notificationService;

  // 신규 알림 등록 (테스트용, 실제는 이벤트나 스케줄러에서 호출)
  @PostMapping("/send")
  public void send(@RequestBody NotificationCreateDTO dto) {
    // 현재 로그인한 사용자에게 알림 생성
    notificationService.send(dto);
  }

  // 로그인한 사용자 기준 전체 알림 조회
  @GetMapping("/my")
  public List<NotificationResponseDTO> getMyNotifications() {
    return notificationService.getAllByCurrentMember();
  }


  // 단일 조회
  @GetMapping("/{notificationId}")
  public NotificationResponseDTO getOne(@PathVariable Long notificationId) {
    return notificationService.getOne(notificationId);
  }


  // 단일 알림 읽음 처리
  @PatchMapping("/{notificationId}/read")
  public void markAsRead(@PathVariable Long notificationId) {
    notificationService.markAsRead(notificationId);
  }

  // 선택 알림 읽음 처리 (체크박스 기반)
  @PatchMapping("/read-selected")
  public void markSelectedAsRead(@RequestBody NotificationBatchRequestDTO dto) {
    notificationService.markSelectedAsRead(dto);
  }

  // 전체 알림 읽음 처리
  @PatchMapping("/read-all")
  public void markAllAsRead() {
    notificationService.markAllAsRead();
  }

  // 단일 알림 삭제
  @DeleteMapping("/{notificationId}")
  public void delete(@PathVariable Long notificationId) {
    notificationService.delete(notificationId);
  }

  // 선택 알림 삭제 (체크박스 기반)
  @DeleteMapping("/delete-selected")
  public void deleteSelected(@RequestBody NotificationBatchRequestDTO dto) {
    notificationService.deleteSelected(dto);
  }

  // 전체 알림 삭제
  @DeleteMapping("/delete-all")
  public void deleteAll() {
    notificationService.deleteAllByCurrentMember();
  }

}
