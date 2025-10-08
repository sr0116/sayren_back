package com.imchobo.sayren_back.domain.subscribe.subscribe_round.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentType;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentMapper;
import com.imchobo.sayren_back.domain.payment.service.PaymentService;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeStatusChanger;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeRoundDueEvent;
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

@Service
@RequiredArgsConstructor
@Log4j2
public class SubscribeRoundScheduler {

  private final SubscribeRoundRepository subscribeRoundRepository;
  private final SubscribeStatusChanger subscribeStatusChanger;
  private final ApplicationEventPublisher eventPublisher;

  // 매일 새벽 5시마다 실행
//  @Scheduled(cron = "0 0 5 * * *")
//  @Scheduled(fixedRate = 30000) // 테스트: 30초마다 실행
  @Transactional
  public void processDueRounds() {
    LocalDate today = LocalDate.now();
    LocalDateTime now = LocalDateTime.now();

    // 결제 예정 회차 조회(오늘 + 결제 대기중)
    List<SubscribeRound> dueRounds =
            subscribeRoundRepository.findByDueDateAndPayStatus(today, PaymentStatus.PENDING);

    if(dueRounds.isEmpty()) {
      log.info("오늘 결제 예정 회차가 없습니다.");
      return;
    }

    for (SubscribeRound round : dueRounds) {
      try {
        //실제 결제 말고 알림만
        log.info("결제 예정 회차 감지: subscribeRoundId={}, memberId={}, dueDate={}",
                round.getId(),
                round.getSubscribe().getMember().getId(),
                round.getDueDate());

        // 나중에 알림 이벤트 붙이기
        // 회차 이벤트 발행
        eventPublisher.publishEvent(new SubscribeRoundDueEvent(round));
      } catch (Exception e) {
        log.error("결제 예정 회차 처리 중 오류 발생: roundId={}, message={}", round.getId(), e.getMessage());
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

    // 유예 기간 만료 검사 후 상태 변경
    // 2) 유예기간 만료 검사 (FAILED + gracePeriodEndAt < now)
    List<SubscribeRound> failedRounds =
            subscribeRoundRepository.findByPayStatusIn(List.of(
                    PaymentStatus.FAILED, PaymentStatus.PENDING));

    for (SubscribeRound round : failedRounds) {
      if (round.getGracePeriodEndAt() != null && round.getGracePeriodEndAt().isBefore(now)) {
        log.info("[유예기간 종료] 구독={}, 회차={}, gracePeriodEndAt={}",
                round.getSubscribe().getId(), round.getRoundNo(), round.getGracePeriodEndAt());

        // (추가) 구독 전체 상태를 연체로 전환
        subscribeStatusChanger.changeSubscribe(
                round.getSubscribe(),
                SubscribeTransition.OVERDUE_FINAL,
                ActorType.SYSTEM
        );

        // 해당 회차도 전체 failed 처리
        round.setPayStatus(PaymentStatus.FAILED);
        subscribeRoundRepository.save(round);

        log.info("유예 기간 만료 처리 구독={}, 회차={}",
                round.getSubscribe().getId(), round.getRoundNo());
      }
    }
  }
}
