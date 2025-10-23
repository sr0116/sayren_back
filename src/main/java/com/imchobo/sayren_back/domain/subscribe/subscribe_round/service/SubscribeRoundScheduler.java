package com.imchobo.sayren_back.domain.subscribe.subscribe_round.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.notification.service.NotificationService;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeStatusChanger;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeRoundDueEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribeRoundScheduler {

  private final SubscribeRoundRepository subscribeRoundRepository;
  private final SubscribeStatusChanger subscribeStatusChanger;
  private final ApplicationEventPublisher eventPublisher;
  private final NotificationService notificationService;

  // 이미 알림 보낸 회차 방지용 캐시
  private final Set<Long> notifiedRoundIds = ConcurrentHashMap.newKeySet();

  /**
   * 회차 결제 스케줄러
   * - 매 1분마다 실행 (테스트용)
   * - 배포 시 cron: 0 0 5 * * * (매일 새벽 5시)
   */
//  @Scheduled(fixedRate = 60000)
  @Transactional
  public void processDueRounds() {
    LocalDate today = LocalDate.now();
    log.info("[Scheduler] 실행 시간: {}", LocalDateTime.now());

    // 1. 오늘 결제 예정 회차 조회
    List<SubscribeRound> dueRounds =
            subscribeRoundRepository.findByDueDateAndPayStatus(today, PaymentStatus.PENDING);

    if (dueRounds.isEmpty()) {
      log.info("오늘 결제 예정 회차가 없습니다. (날짜={})", today);
      return;
    }

    log.info("오늘 결제 예정 회차 {}건 발견 (날짜={})", dueRounds.size(), today);

    // 2. 결제 예정 알림 발행 (동기 이벤트)
    for (SubscribeRound round : dueRounds) {
      try {
        if (notifiedRoundIds.contains(round.getId())) {
          log.debug("이미 알림 발송된 회차 → skip: roundId={}", round.getId());
          continue;
        }

        log.info("결제 예정 회차 감지: subscribeRoundId={}, memberId={}, dueDate={}",
                round.getId(),
                round.getSubscribe().getMember().getId(),
                round.getDueDate());

        // 동기 이벤트 발행 (세션 내에서 즉시 실행)
        eventPublisher.publishEvent(new SubscribeRoundDueEvent(round, "DUE"));

        notifiedRoundIds.add(round.getId());
        log.info("[INFO] 결제 예정 알림 완료 → roundId={} (cacheSize={})",
                round.getId(), notifiedRoundIds.size());

      } catch (Exception e) {
        log.error("[ERROR] 결제 예정 회차 처리 중 오류: roundId={}, message={}",
                round.getId(), e.getMessage());
      }
    }

    // 3. 유예기간 관련 처리
    handleGracePeriodRounds();
  }

  /**
   * 유예기간 만료 및 연체 처리
   */
  private void handleGracePeriodRounds() {
    LocalDateTime now = LocalDateTime.now();

    List<SubscribeRound> targetRounds =
            subscribeRoundRepository.findByPayStatusIn(List.of(PaymentStatus.FAILED, PaymentStatus.PENDING));

    for (SubscribeRound round : targetRounds) {
      try {
        if (round.getGracePeriodEndAt() == null) continue;

        LocalDateTime warningTime = round.getGracePeriodEndAt().minusDays(1);

        // 하루 전 경고
        if (now.isAfter(warningTime) && now.isBefore(round.getGracePeriodEndAt())) {
          log.info("[유예 만료 하루 전] 구독={}, 회차={}, gracePeriodEndAt={}",
                  round.getSubscribe().getId(), round.getRoundNo(), round.getGracePeriodEndAt());

          eventPublisher.publishEvent(new SubscribeRoundDueEvent(round, "WARNING"));
        }

        // 유예기간 만료 시점
        if (round.getGracePeriodEndAt().isBefore(now)) {
          log.info("[유예기간 종료] 구독={}, 회차={}, gracePeriodEndAt={}",
                  round.getSubscribe().getId(), round.getRoundNo(), round.getGracePeriodEndAt());

          // 회차 상태 변경
          subscribeStatusChanger.changeSubscribeRound(round, SubscribeRoundTransition.PAY_FAIL);

          // 구독 전체 연체 상태로 변경
          subscribeStatusChanger.changeSubscribe(
                  round.getSubscribe(),
                  SubscribeTransition.OVERDUE_FINAL,
                  ActorType.SYSTEM
          );

          eventPublisher.publishEvent(new SubscribeRoundDueEvent(round, "OVERDUE"));
          log.info("[알림 발행] 연체 확정 알림 완료 → subscribeId={}, roundNo={}",
                  round.getSubscribe().getId(), round.getRoundNo());
        }

      } catch (Exception e) {
        log.error("[ERROR] 유예기간 처리 실패 → roundId={}, message={}", round.getId(), e.getMessage());
      }
    }
  }
}
