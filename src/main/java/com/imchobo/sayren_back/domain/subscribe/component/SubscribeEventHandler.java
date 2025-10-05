package com.imchobo.sayren_back.domain.subscribe.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeNotFoundException;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeHistoryRepository;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
@Log4j2
public class SubscribeEventHandler {

  private final SubscribeRoundRepository subscribeRoundRepository;
  private final PaymentRepository paymentRepository;
  private final SubscribeHistoryRepository subscribeHistoryRepository;
  private final SubscribeRepository subscribeRepository;
  private final SubscribeStatusChanger subscribeStatusChanger;
  private final DeliveryItemRepository deliveryItemRepository;

  // 최초 구독 생성 시 기록
  public void recordInit(Subscribe subscribe) {
    SubscribeHistory history = SubscribeHistory.builder()
            .subscribe(subscribe)
            .status(subscribe.getStatus()) // PENDING_PAYMENT
            .reasonCode(ReasonCode.NONE)
            .build();
    subscribeHistoryRepository.save(history);
  }

  //  구독 상태 변경 (히스토리 이벤트 핸들러)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSubscribeStatusChanged(SubscribeStatusChangedEvent event) {
    log.info("[EVENT] handleSubscribeStatusChanged triggered → subscribeId={}, transition={}, actor={}",
            event.getSubscribeId(),
            event.getTransition(),
            event.getActor());

    // transition null 방어 로그
    if (event.getTransition() == null) {
      log.error("[FATAL] SubscribeStatusChangedEvent.transition is NULL (subscribeId={})", event.getSubscribeId());
      return;
    }

    if (event.getTransition().getStatus() == null || event.getTransition().getReason() == null) {
      log.error("[FATAL] Transition 내부 필드 null → status={}, reason={}, subscribeId={}",
              event.getTransition().getStatus(),
              event.getTransition().getReason(),
              event.getSubscribeId());
    }

    // 1) 구독 엔티티 조회
    Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId())
            .orElseThrow(() -> new SubscribeNotFoundException(event.getSubscribeId()));

    // 2) 이력 생성
    SubscribeHistory history = SubscribeHistory.builder()
            .subscribe(subscribe)
            .status(event.getTransition().getStatus())
            .reasonCode(event.getTransition().getReason())
            .changedBy(event.getActor())
            .build();

    // 3) 저장
    subscribeHistoryRepository.save(history);
    log.info("[HISTORY] 구독 이력 저장 완료 → subscribeId={}, status={}, reason={}, actor={}",
            subscribe.getId(),
            history.getStatus(),
            history.getReasonCode(),
            history.getChangedBy());
  }

  // 결제 상태 변경 → 구독 회차 연동
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePaymentStatusChanged(PaymentStatusChangedEvent event) {
    log.info("[EVENT] handlePaymentStatusChanged → paymentId={}, transition={}", event.getPaymentId(), event.getTransition());

    paymentRepository.findById(event.getPaymentId())
            .map(Payment::getSubscribeRound)
            .ifPresent(round -> {
              SubscribeRoundTransition transition = mapToRoundTransition(event.getTransition());
              Subscribe subscribe = round.getSubscribe();

              if (transition == null) {
                log.error("[FATAL] SubscribeRoundTransition mapping 실패: PaymentTransition={}, paymentId={}",
                        event.getTransition(), event.getPaymentId());
                return;
              }

              // 동일 상태 중복 방지
              if (round.getPayStatus() == transition.getStatus()) {
                log.debug("중복 이벤트 무시 - roundId={}, status={}", round.getId(), round.getPayStatus());
                return;
              }

              switch (transition) {
                case PAY_SUCCESS, RETRY_SUCCESS -> {
                  round.setPayStatus(transition.getStatus());
                  round.setPaidDate(LocalDateTime.now());
                  round.setFailedAt(null);
                  round.setGracePeriodEndAt(null);
                  subscribeRoundRepository.save(round);

                  boolean canPrepare = deliveryItemRepository
                          .findByOrderItem(subscribe.getOrderItem())
                          .stream()
                          .anyMatch(di -> di.getDelivery().getStatus() == DeliveryStatus.READY);

                  boolean canActivate = deliveryItemRepository
                          .findByOrderItem(subscribe.getOrderItem())
                          .stream()
                          .anyMatch(di -> di.getDelivery().getStatus() == DeliveryStatus.DELIVERED);

                  if (round.getRoundNo() == 1) {
                    if (canPrepare) {
                      subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.PREPARE, ActorType.SYSTEM);
                      log.info("구독 [{}] 첫 회차 결제 성공 → PREPARING 전환", subscribe.getId());
                    } else if (canActivate) {
                      subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.START, ActorType.SYSTEM);
                      log.info("구독 [{}] 첫 회차 결제 + 배송 완료 → ACTIVE 전환", subscribe.getId());
                    } else {
                      log.info("구독 [{}] 첫 회차 결제 완료, 배송 상태 불충족 (roundNo=1)", subscribe.getId());
                    }
                  } else {
                    log.info("구독 [{}] {}회차 결제 성공", subscribe.getId(), round.getRoundNo());
                  }
                }

                case PAY_FAIL, RETRY_FAIL -> {
                  round.setPayStatus(transition.getStatus());
                  round.setFailedAt(LocalDateTime.now());
                  round.setGracePeriodEndAt(LocalDateTime.now().plusDays(3));
                  subscribeRoundRepository.save(round);

                  if (round.getRoundNo() == 1) {
                    failAllRounds(subscribe, transition);
                    subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.FAIL_PAYMENT, ActorType.SYSTEM);
                    log.info("구독 [{}] 1회차 결제 실패 → 전체 FAILED", subscribe.getId());
                  } else {
                    log.info("구독 [{}] {}회차 결제 실패 → 유예기간 3일 시작", subscribe.getId(), round.getRoundNo());
                  }
                }

                case PAY_TIMEOUT -> {
                  round.setPayStatus(transition.getStatus());
                  subscribeRoundRepository.save(round);
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.OVERDUE_FINAL, ActorType.SYSTEM);
                  log.info("구독 [{}] 결제 타임아웃 → OVERDUE_FINAL", subscribe.getId());
                }

                case INIT_FAIL -> {
                  failAllRounds(subscribe, transition);
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.FAIL_PAYMENT, ActorType.SYSTEM);
                  log.info("구독 [{}] INIT_FAIL → 전체 FAILED", subscribe.getId());
                }

                case CANCEL_ALL -> {
                  cancelAllRounds(subscribe, transition);
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.RETURNED_AND_CANCELED, ActorType.SYSTEM);
                  log.info("구독 [{}] 전체 CANCEL 처리", subscribe.getId());
                }

                case FORCED_END -> {
                  failAllRounds(subscribe, transition);
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.OVERDUE_FINAL, ActorType.SYSTEM);
                  log.info("구독 [{}] FORCED_END → 전체 FAILED", subscribe.getId());
                }
              }
            });
  }

  private void failAllRounds(Subscribe subscribe, SubscribeRoundTransition transition) {
    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
    LocalDateTime now = LocalDateTime.now();

    rounds.forEach(r -> {
      r.setPayStatus(transition.getStatus());
      r.setFailedAt(now);
      r.setGracePeriodEndAt(now.plusDays(3));
    });

    subscribeRoundRepository.saveAll(rounds);
  }

  private void cancelAllRounds(Subscribe subscribe, SubscribeRoundTransition transition) {
    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
    rounds.forEach(r -> r.setPayStatus(transition.getStatus()));
    subscribeRoundRepository.saveAll(rounds);
  }

  private SubscribeRoundTransition mapToRoundTransition(PaymentTransition transition) {
    if (transition == null) {
      log.error("[FATAL] PaymentTransition이 null → SubscribeRoundTransition 매핑 실패");
      return null;
    }
    return switch (transition) {
      case COMPLETE -> SubscribeRoundTransition.PAY_SUCCESS;
      case FAIL_USER, FAIL_PAYMENT, FAIL_SYSTEM -> SubscribeRoundTransition.PAY_FAIL;
      case FAIL_TIMEOUT -> SubscribeRoundTransition.PAY_TIMEOUT;
      case REFUND, PARTIAL_REFUND -> SubscribeRoundTransition.CANCEL;
      default -> SubscribeRoundTransition.PAY_FAIL;
    };
  }
}
