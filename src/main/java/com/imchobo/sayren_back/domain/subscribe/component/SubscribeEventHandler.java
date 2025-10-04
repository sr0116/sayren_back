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
  // 이후에 분리하는 방향이고 지금은 세가지 테이블 한 번에 핸들러 쪽에 모아둠
  // 이벤트 리스너
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final PaymentRepository paymentRepository;
  private final SubscribeHistoryRepository subscribeHistoryRepository;
  private final SubscribeRepository subscribeRepository;
  private final SubscribeStatusChanger subscribeStatusChanger;
  private final DeliveryItemRepository deliveryItemRepository;

  // 구독 이력 기록 및 상태(히스토리 테이블 첫 생성)
  public void recordInit(Subscribe subscribe) {
    SubscribeHistory history = SubscribeHistory.builder().subscribe(subscribe).status(subscribe.getStatus())   // PENDING_PAYMENT
            .reasonCode(ReasonCode.NONE).build();
    subscribeHistoryRepository.save(history);
  }

  // 구독 상태 변경 (히스토리)이벤트 핸들러
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSubscribeStatusChanged(SubscribeStatusChangedEvent event) {
    // 1) 구독 엔티티 조회 (FK 저장용)
    Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId()).orElseThrow(() -> new SubscribeNotFoundException(event.getSubscribeId()));

    // 2) 이력 엔티티 생성
    SubscribeHistory history = SubscribeHistory.builder().subscribe(subscribe).status(event.getTransition().getStatus())    // 상태
            .reasonCode(event.getTransition().getReason())// 변경 이유
            .build();

    // 3) 저장
    subscribeHistoryRepository.save(history);
  }

  // 구독 회차 상태 변경 이벤트 헨들러
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePaymentStatusChanged(PaymentStatusChangedEvent event) {
    //
    paymentRepository.findById(event.getPaymentId())
            .map(Payment::getSubscribeRound)
            .ifPresent(round -> {

              // 일반 결제인 경우 구독 회차가 null일 수 있으므로 필터링
              SubscribeRoundTransition transition = mapToRoundTransition(event.getTransition());
              Subscribe subscribe = round.getSubscribe();

              // 중복 이벤트 방지: 이미 같은 상태면 무시
              if (round.getPayStatus() == transition.getStatus()) {
                log.debug("중복 이벤트 무시 - roundId={}, status={}", round.getId(), round.getPayStatus());
                return;
              }

              switch (transition) {
                case PAY_SUCCESS, RETRY_SUCCESS -> {
                  // 회차 상태 갱신
                  round.setPayStatus(transition.getStatus());
                  round.setPaidDate(LocalDateTime.now());
                  // 재결제 성공 시 유예기간 초기화
                  round.setFailedAt(null);
                  round.setGracePeriodEndAt(null);
                  subscribeRoundRepository.save(round);

                  // 배송 상태 조회
                  boolean canPrepare = deliveryItemRepository
                          .findByOrderItem(subscribe.getOrderItem())
                          .stream()
                          .anyMatch(di -> di.getDelivery().getStatus() == DeliveryStatus.READY);

                  boolean canActivate = deliveryItemRepository
                          .findByOrderItem(subscribe.getOrderItem())
                          .stream()
                          .anyMatch(di -> di.getDelivery().getStatus() == DeliveryStatus.DELIVERED);

                  // 구독 첫 회차 결제시
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
                // 결제 실패  _ 재시도 실패
                case PAY_FAIL, RETRY_FAIL -> {
                  round.setPayStatus(transition.getStatus());
                  // 결제 실패 시 유예기간 설정
                  round.setFailedAt(LocalDateTime.now());
                  round.setGracePeriodEndAt(LocalDateTime.now().plusDays(3));
                  subscribeRoundRepository.save(round);
                  // 1회차 시에는 구독 전체 실패
                  if (round.getRoundNo() == 1) {
                    failAllRounds(subscribe, transition);
                    subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.FAIL_PAYMENT, ActorType.SYSTEM);
                    log.info("구독 [{}] 1회차 결제 실패 → 전체 FAILED", subscribe.getId());
                  } else {
                    // 즉시 연체 전환 X → 유예기간 내 재시도 가능
                    log.info("구독 [{}] {}회차 결제 실패 → 유예기간 시작 (3일 이내 재시도 가능)",
                            subscribe.getId(), round.getRoundNo());
                  }
                }

                // 결제 타임아웃 (유예기간 초과)
                case PAY_TIMEOUT -> {
                  round.setPayStatus(transition.getStatus());
                  subscribeRoundRepository.save(round);

                  // 상태명 통일: OVERDUE_FINAL
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.OVERDUE_FINAL, ActorType.SYSTEM);
                  log.info("구독 [{}] 결제 타임아웃 → OVERDUE_FINAL", subscribe.getId());
                }
                // 초기 결제 실패
                case INIT_FAIL -> {
                  failAllRounds(subscribe, transition);
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.FAIL_PAYMENT, ActorType.SYSTEM);
                  log.info("구독 [{}] INIT_FAIL → 전체 FAILED", subscribe.getId());
                }
                // 전체 취소(환불 또는 계약 해지)
                case CANCEL_ALL -> {
                  cancelAllRounds(subscribe, transition);
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.RETURNED_AND_CANCELED, ActorType.SYSTEM);
                  log.info("구독 [{}] 전체 CANCEL 처리", subscribe.getId());
                }
                // 강제 종료
                // 강제 종료
                case FORCED_END -> {
                  failAllRounds(subscribe, transition);
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.OVERDUE_FINAL, ActorType.SYSTEM);
                  log.info("구독 [{}] FORCED_END → 전체 FAILED", subscribe.getId());
                }
              }
            });

  }

  // 전체 회차 실패 처리
  private void failAllRounds(Subscribe subscribe, SubscribeRoundTransition transition) {
    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
    LocalDateTime now = LocalDateTime.now();

    rounds.forEach(r -> {
      r.setPayStatus(transition.getStatus());
      // 전체 실패 시 유예기간도 설정
      r.setFailedAt(now);
      r.setGracePeriodEndAt(now.plusDays(3));
    });

    subscribeRoundRepository.saveAll(rounds);
  }

  // 전체 회차 취소 처리
  private void cancelAllRounds(Subscribe subscribe, SubscribeRoundTransition transition) {
    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
    rounds.forEach(r -> r.setPayStatus(transition.getStatus()));
    subscribeRoundRepository.saveAll(rounds);
  }
  // PaymentTransition → SubscribeRoundTransition 매핑

  private SubscribeRoundTransition mapToRoundTransition(PaymentTransition transition) {
    return switch (transition) {
      case COMPLETE -> SubscribeRoundTransition.PAY_SUCCESS;
      case FAIL_USER, FAIL_PAYMENT, FAIL_SYSTEM -> SubscribeRoundTransition.PAY_FAIL;
      case FAIL_TIMEOUT -> SubscribeRoundTransition.PAY_TIMEOUT;
      case REFUND, PARTIAL_REFUND -> SubscribeRoundTransition.CANCEL;
      default -> SubscribeRoundTransition.PAY_FAIL; // 안전 fallback (누락 case 방지)
    };
  }
}
