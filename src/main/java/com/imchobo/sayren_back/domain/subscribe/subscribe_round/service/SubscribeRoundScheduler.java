package com.imchobo.sayren_back.domain.subscribe.subscribe_round.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.notification.dto.NotificationCreateDTO;
import com.imchobo.sayren_back.domain.notification.service.NotificationService;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentType;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentMapper;
import com.imchobo.sayren_back.domain.payment.service.PaymentService;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeStatusChanger;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeRoundDueEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
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

  // 테스트용으로  추가
  // 이미 알림을 발송한 회차 ID 저장 (메모리 캐시)
  private final Set<Long> notifiedRoundIds = ConcurrentHashMap.newKeySet();
  private final NotificationService notificationService;

  // 매일 새벽 5시마다 실행
//  @Scheduled(cron = "0 0 5 * * *")
//  @Scheduled(fixedRate = 60000) // 테스트: 30초마다 실행
  @Transactional
  public void processDueRounds() {
    LocalDate today = LocalDate.now();
    LocalDateTime now = LocalDateTime.now();

    // 결제 예정 회차 조회(오늘 + 결제 대기중)
    List<SubscribeRound> dueRounds =
            subscribeRoundRepository.findByDueDateAndPayStatus(today, PaymentStatus.PENDING);

    if (dueRounds.isEmpty()) {
      log.info("오늘 결제 예정 회차가 없습니다.");
    } else {
      log.info("오늘 결제 예정 회차 {}건 발견 (날짜={})", dueRounds.size(), today);
    }
    // 결제 예정 회차 알림(중복 방지 - 테스트 용도라서 조건추가)
    for (SubscribeRound round : dueRounds) {
      try {
        // 이미 알림 발송된 회차는 skip 나중에 로그는 전부 삭제
        if (notifiedRoundIds.contains(round.getId())) {
          log.debug("이미 알림 발송된 회차 → skip: roundId={}", round.getId());
          continue;
        }
        //실제 결제 말고 알림만
        log.info("결제 예정 회차 감지: subscribeRoundId={}, memberId={}, dueDate={}",
                round.getId(),
                round.getSubscribe().getMember().getId(),
                round.getDueDate());

        // 회차 이벤트 발행 (알림 이벤트)
        eventPublisher.publishEvent(new SubscribeRoundDueEvent(round, "DUE"));

        // 중복 방지 처리
        notifiedRoundIds.add(round.getId());
        log.info("[INFO] 결제 예정 알림 완료 → roundId={} (notifiedRoundIds.size={})",
                round.getId(), notifiedRoundIds.size());
      } catch (Exception e) {
        log.error("결제 예정 회차 처리 중 오류 발생: roundId={}, message={}", round.getId(), e.getMessage());
      }
    }

    // 유예 기간 만료 검사 후 상태 변경
    // 유예기간 하루 전 경고 알림
    List<SubscribeRound> warningRounds =
            subscribeRoundRepository.findByPayStatusIn(List.of(PaymentStatus.FAILED, PaymentStatus.PENDING));
    for (SubscribeRound round : warningRounds) {
      try {
        if (round.getGracePeriodEndAt() == null) continue;

        LocalDateTime warningTime = round.getGracePeriodEndAt().minusDays(1);

        // "만료 하루 전" 구간 내에 있을 때만 발행
        if (now.isAfter(warningTime) && now.isBefore(round.getGracePeriodEndAt())) {
          log.info("[유예 만료 하루 전] 구독={}, 회차={}, gracePeriodEndAt={}",
                  round.getSubscribe().getId(), round.getRoundNo(), round.getGracePeriodEndAt());

          eventPublisher.publishEvent(new SubscribeRoundDueEvent(round, "WARNING"));
        }
      } catch (Exception e) {
        log.error("[ERROR] 유예 하루 전 경고 알림 처리 실패 → roundId={}, message={}",
                round.getId(), e.getMessage());
      }
    }

    // 유예 기간 3일 후 만료 처리 (연체 확정)
    List<SubscribeRound> failedRounds =
            subscribeRoundRepository.findByPayStatusIn(List.of(PaymentStatus.FAILED, PaymentStatus.PENDING));

    for (SubscribeRound round : failedRounds) {
      try {
        if (round.getGracePeriodEndAt() != null && round.getGracePeriodEndAt().isBefore(now)) {
          log.info("[유예기간 종료] 구독={}, 회차={}, gracePeriodEndAt={}",
                  round.getSubscribe().getId(), round.getRoundNo(), round.getGracePeriodEndAt());

          // 회차 연체 상태 전환
          subscribeStatusChanger.changeSubscribeRound(round, SubscribeRoundTransition.PAY_FAIL);

          // 구독 전체 연체 전환
          subscribeStatusChanger.changeSubscribe(
                  round.getSubscribe(),
                  SubscribeTransition.OVERDUE_FINAL,
                  ActorType.SYSTEM
          );

          // 이벤트 발행 (OVERDUE)
          eventPublisher.publishEvent(new SubscribeRoundDueEvent(round, "OVERDUE"));

          log.info("[알림 발행] 연체 확정 알림 이벤트 발행 완료 → subscribeId={}, roundNo={}",
                  round.getSubscribe().getId(), round.getRoundNo());
        }
      } catch (Exception e) {
        log.error("[ERROR] 유예기간 만료 처리 실패 → roundId={}, message={}", round.getId(), e.getMessage());
      }
    }
  }
}


// 실제 빌링키 연결한다고 했을 때 기준 (지금은 사용 안함)
//    for (SubscribeRound round : dueRounds) {
//      try {
//        log.info("회차 결제 준비: subscribeId={}, roundNo={}, amount={}",
//                round.getSubscribe().getId(), round.getRoundNo(), round.getAmount());
//
//        //  회차 기준 Payment 생성 (PENDING 상태)
//        PaymentResponseDTO paymentDto = paymentService.prepareForRound(round);
//
//        //  로그만 남기고, 결제 완료는 프론트에서 impUid 전달 후 처리
//        log.info("결제 준비 완료: paymentId={}, merchantUid={}, amount={}",
//                paymentDto.getPaymentId(), paymentDto.getMerchantUid(), paymentDto.getAmount());
//
//      } catch (Exception e) {
//        log.error("회차 결제 준비 실패: roundId={}, 이유={}", round.getId(), e.getMessage());
//        // 유예 기간 설정 (3일로 일단 고정)
//        round.setPayStatus(PaymentStatus.PENDING); // 바로 결제 실패 하지 말고 유예기간 3일정도
//        round.setFailedAt(LocalDateTime.now()); // 실패 시각 기록
//        round.setGracePeriodEndAt(LocalDateTime.now().plusDays(3)); // 3일 유예기간 계산
//        subscribeRoundRepository.save(round);
//
//        log.warn("스케쥴러 결제 실패 유예 기간 3일 - roundId={}" , round.getId());
//      }
//    }
