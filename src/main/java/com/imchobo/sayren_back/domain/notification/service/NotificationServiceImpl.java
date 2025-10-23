package com.imchobo.sayren_back.domain.notification.service;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.notification.dto.NotificationBatchRequestDTO;
import com.imchobo.sayren_back.domain.notification.dto.NotificationCreateDTO;
import com.imchobo.sayren_back.domain.notification.dto.NotificationResponseDTO;
import com.imchobo.sayren_back.domain.notification.en.NotificationStatus;
import com.imchobo.sayren_back.domain.notification.entity.Notification;
import com.imchobo.sayren_back.domain.notification.mapper.NotificationMapper;
import com.imchobo.sayren_back.domain.notification.repository.NotificationRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class NotificationServiceImpl implements NotificationService {

  private final NotificationMapper notificationMapper;
  private final NotificationRepository notificationRepository;
  private final MemberRepository memberRepository;

  // 단일 알림 생성
  @Override
  public void send(NotificationCreateDTO dto) {
    // memberId로 회원 조회
    Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new IllegalStateException("알림 전송 대상 회원을 찾을 수 없습니다. memberId=" + dto.getMemberId()));

    // DTO → 엔티티 변환
    Notification notification = notificationMapper.toEntity(dto);
    notification.setMember(member);

    // 저장
    notificationRepository.save(notification);

    log.info("알림 생성 완료: memberId={}, type={}, title={}",
            member.getId(), dto.getType(), dto.getTitle());
  }

  // 로그인한 사용자의 알림 전체 조회
  @Override
  @Transactional(readOnly = true)
  public List<NotificationResponseDTO> getAllByCurrentMember() {
    Member currentMember = com.imchobo.sayren_back.security.util.SecurityUtil.getMemberEntity();
    List<Notification> notifications = notificationRepository.findByMemberOrderByRegDateDesc(currentMember);
    return notificationMapper.toDTOList(notifications);
  }

  // 단일 알림 조회
  @Override
  @Transactional(readOnly = true)
  public NotificationResponseDTO getOne(Long notificationId) {
    Member currentMember = com.imchobo.sayren_back.security.util.SecurityUtil.getMemberEntity();

    Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("해당 알림을 찾을 수 없습니다. id=" + notificationId));

    // 본인 알림인지 검증
    if (!notification.getMember().getId().equals(currentMember.getId())) {
      throw new SecurityException("다른 사용자의 알림은 조회할 수 없습니다.");
    }

    return notificationMapper.toDTO(notification);
  }


  // 단일 알림 읽음 처리
  @Override
  public void markAsRead(Long notificationId) {
    Member currentMember = com.imchobo.sayren_back.security.util.SecurityUtil.getMemberEntity();
    notificationRepository.findById(notificationId).ifPresent(notification -> {
      if (notification.getMember().getId().equals(currentMember.getId())) {
        notification.setStatus(NotificationStatus.READ);
        log.info("알림 읽음 처리 완료: memberId={}, notificationId={}", currentMember.getId(), notificationId);
      }
    });
  }

  // 선택 알림 읽음 처리
  @Override
  public void markSelectedAsRead(NotificationBatchRequestDTO dto) {
    Member currentMember = com.imchobo.sayren_back.security.util.SecurityUtil.getMemberEntity();
    List<Notification> notifications = notificationRepository.findAllById(dto.getNotificationIds());

    notifications.stream()
            .filter(n -> n.getMember().getId().equals(currentMember.getId()))
            .forEach(n -> n.setStatus(NotificationStatus.READ));

    log.info("선택 알림 읽음 처리 완료: memberId={}, count={}", currentMember.getId(), dto.getNotificationIds().size());
  }

  // 전체 알림 읽음 처리
  @Override
  public void markAllAsRead() {
    Member currentMember = com.imchobo.sayren_back.security.util.SecurityUtil.getMemberEntity();
    List<Notification> notifications = notificationRepository.findByMemberOrderByRegDateDesc(currentMember);
    notifications.forEach(n -> n.setStatus(NotificationStatus.READ));
    log.info("전체 알림 읽음 처리 완료: memberId={}, count={}", currentMember.getId(), notifications.size());
  }

  // 단일 알림 삭제
  @Override
  public void delete(Long notificationId) {
    Member currentMember = com.imchobo.sayren_back.security.util.SecurityUtil.getMemberEntity();
    notificationRepository.findById(notificationId).ifPresent(notification -> {
      if (notification.getMember().getId().equals(currentMember.getId())) {
        notificationRepository.delete(notification);
        log.info("알림 삭제 완료: memberId={}, notificationId={}", currentMember.getId(), notificationId);
      }
    });
  }

  // 선택 알림 삭제
  @Override
  public void deleteSelected(NotificationBatchRequestDTO dto) {
    Member currentMember = com.imchobo.sayren_back.security.util.SecurityUtil.getMemberEntity();
    List<Notification> notifications = notificationRepository.findAllById(dto.getNotificationIds());

    List<Notification> owned = notifications.stream()
            .filter(n -> n.getMember().getId().equals(currentMember.getId()))
            .toList();

    notificationRepository.deleteAll(owned);
    log.info("선택 알림 삭제 완료: memberId={}, count={}", currentMember.getId(), owned.size());
  }

  // 전체 알림 삭제
  @Override
  public void deleteAllByCurrentMember() {
    Member currentMember = com.imchobo.sayren_back.security.util.SecurityUtil.getMemberEntity();
    List<Notification> notifications = notificationRepository.findByMemberOrderByRegDateDesc(currentMember);
    notificationRepository.deleteAll(notifications);
    log.info("전체 알림 삭제 완료: memberId={}, count={}", currentMember.getId(), notifications.size());
  }
}
