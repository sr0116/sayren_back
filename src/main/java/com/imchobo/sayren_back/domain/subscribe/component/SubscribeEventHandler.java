package com.imchobo.sayren_back.domain.subscribe.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.component.event.DeliveryStatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.notification.dto.NotificationCreateDTO;
import com.imchobo.sayren_back.domain.notification.en.NotificationType;
import com.imchobo.sayren_back.domain.notification.service.NotificationService;
import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeActivatedEvent;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeRoundDueEvent;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedReasonEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
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
  private final DeliveryItemRepository deliveryItemRepository;
  private final NotificationService notificationService;
  private final SubscribeStatusChanger subscribeStatusChanger;
  private final ApplicationEventPublisher eventPublisher;

  // 구독이 처음 생성될 때 기본 이력 기록 (PENDING_PAYMENT 상태)
  public void recordInit(Subscribe subscribe) {
    SubscribeHistory history = SubscribeHistory.builder()
            .subscribe(subscribe)
            .status(subscribe.getStatus())
            .reasonCode(ReasonCode.NONE)
            .changedBy(ActorType.SYSTEM)
            .build();
    subscribeHistoryRepository.save(history);
  }

  // 구독 상태 변경 이벤트 처리 (시스템, 사용자, 관리자 모두 포함)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleSubscribeStatusChanged(SubscribeStatusChangedEvent event) {
    processStatusChange(
            event.getSubscribeId(),
            event.getTransition(),
            event.getActor(),
            event.getTransition().getReason()
    );
  }

  // 구독 상태 변경 이벤트 (Reason 포함)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleSubscribeStatusChangedReason(SubscribeStatusChangedReasonEvent event) {
    processStatusChange(
            event.getSubscribeId(),
            event.getTransition(),
            event.getActor(),
            event.getReasonCode() != null ? event.getReasonCode() : event.getTransition().getReason()
    );
  }

  // 구독 상태 변경 공통 처리 메서드
  private void processStatusChange(Long subscribeId,
                                   SubscribeTransition transition,
                                   ActorType actor,
                                   ReasonCode reason) {

    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new SubscribeNotFoundException(subscribeId));

    // 이력 저장
    SubscribeHistory history = SubscribeHistory.builder()
            .subscribe(subscribe)
            .status(transition.getStatus())
            .reasonCode(reason != null ? reason : ReasonCode.NONE)
            .changedBy(actor != null ? actor : ActorType.SYSTEM)
            .build();
    subscribeHistoryRepository.save(history);

    log.info("[HISTORY] 구독 이력 기록 완료 → subscribeId={}, status={}, reason={}, actor={}",
            subscribeId, transition.getStatus(), reason, actor);

    if (reason == ReasonCode.EXPIRED) {
      log.info("[SKIP] 계약 만료(EXPIRED) 구독은 회차 상태 변경을 건너뜁니다. (subscribeId={})", subscribeId);
      return;
    }


    // 구독 취소 또는 종료 시 회차 상태 자동 갱신
    switch (transition) {
      case RETURNED_AND_CANCELED, ADMIN_FORCE_END -> {
        List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribeId);
        rounds.forEach(round -> {
          PaymentStatus ps = round.getPayStatus();
          if (ps == PaymentStatus.PENDING ||
                  ps == PaymentStatus.FAILED ||
                  ps == PaymentStatus.CANCELED ||
                  ps == PaymentStatus.PARTIAL_REFUNDED) {
            round.setPayStatus(PaymentStatus.CANCELED);
            round.setFailedAt(LocalDateTime.now());
          }
        });
        subscribeRoundRepository.saveAll(rounds);
        log.info("[ROUND] 구독 종료/취소 시 회차 상태 갱신 완료 ({}건)", rounds.size());
      }

      case FAIL_PAYMENT -> {
        if (subscribe.getStatus() == SubscribeStatus.CANCELED ||
                subscribe.getStatus() == SubscribeStatus.ENDED) return;

        List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribeId);
        rounds.forEach(r -> r.setPayStatus(PaymentStatus.FAILED));
        subscribeRoundRepository.saveAll(rounds);
        log.info("[ROUND] 결제 실패 → 전체 회차 FAILED 처리 완료 (subscribeId={})", subscribeId);
      }

      default -> log.debug("[ROUND] 구독 상태 {}는 회차 갱신 불필요", transition);
    }
  }

  // 결제 상태 변경 → 구독 회차 연동
  @Transactional
  @EventListener
  public void handlePaymentStatusChanged(PaymentStatusChangedEvent event) {
    log.info("[EVENT] handlePaymentStatusChanged → paymentId={}, transition={}",
            event.getPaymentId(), event.getTransition());

    paymentRepository.findById(event.getPaymentId())
            .map(Payment::getSubscribeRound)
            .ifPresent(round -> {
              SubscribeRoundTransition transition = mapToRoundTransition(event.getTransition());
              if (transition == null) return;

              Subscribe subscribe = round.getSubscribe();

              // 중복 방지
              if (round.getPayStatus() == transition.getStatus()) return;

              switch (transition) {
                case PAY_SUCCESS, RETRY_SUCCESS -> {
                  round.setPayStatus(transition.getStatus());
                  round.setPaidDate(LocalDateTime.now());
                  subscribeRoundRepository.save(round);

                  boolean canPrepare = deliveryItemRepository.findByOrderItem(subscribe.getOrderItem())
                          .stream().anyMatch(di -> di.getDelivery().getStatus() == DeliveryStatus.READY);
                  boolean canActivate = deliveryItemRepository.findByOrderItem(subscribe.getOrderItem())
                          .stream().anyMatch(di -> di.getDelivery().getStatus() == DeliveryStatus.DELIVERED);

                  if (round.getRoundNo() == 1) {
                    if (canPrepare) {
                      subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.PREPARE, ActorType.SYSTEM);
                    } else if (canActivate) {
                      subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.START, ActorType.SYSTEM);
                    }
                  }
                }

                case PAY_FAIL, RETRY_FAIL -> {
                  round.setPayStatus(PaymentStatus.PENDING);
                  round.setFailedAt(LocalDateTime.now());
                  subscribeRoundRepository.save(round);

                  if (round.getRoundNo() == 1) {
                    subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.FAIL_PAYMENT, ActorType.SYSTEM);
                  } else {
                    subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.OVERDUE_PENDING, ActorType.SYSTEM);
                  }
                }

                default -> {}
              }
            });
  }

  // PaymentTransition → SubscribeRoundTransition 매핑
  private SubscribeRoundTransition mapToRoundTransition(PaymentTransition transition) {
    if (transition == null) return null;
    return switch (transition) {
      case COMPLETE -> SubscribeRoundTransition.PAY_SUCCESS;
      case FAIL_USER, FAIL_PAYMENT, FAIL_SYSTEM -> SubscribeRoundTransition.PAY_FAIL;
      case FAIL_TIMEOUT -> SubscribeRoundTransition.PAY_TIMEOUT;
      case REFUND, PARTIAL_REFUND, CANCEL_FUTURE_ONLY -> SubscribeRoundTransition.CANCEL;
      default -> null;
    };
  }

  // 배송 완료 시 자동 활성화
  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void onDeliveryStatusChanged(DeliveryStatusChangedEvent event) {
    if (event.getStatus() != DeliveryStatus.DELIVERED) return;

    subscribeRepository.findByOrderItem_Id(event.getOrderItemId())
            .ifPresent(subscribe -> {
              if (subscribe.getStatus() == SubscribeStatus.ACTIVE) return;

              int months = subscribe.getOrderItem().getOrderPlan().getMonth();
              LocalDate start = LocalDate.now();
              LocalDate end = start.plusMonths(months);

              subscribe.setStartDate(start);
              subscribe.setEndDate(end);
              subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.START, ActorType.SYSTEM);

              eventPublisher.publishEvent(new SubscribeActivatedEvent(subscribe.getId(), start));
              log.info("[DELIVERY] 구독 활성화 완료 → subscribeId={}, start={}, end={}", subscribe.getId(), start, end);
            });
  }


  // 회차 알림 이벤트 처리
  @Transactional
  @EventListener
  public void onSubscribeRoundDue(SubscribeRoundDueEvent event) {
    SubscribeRound round = event.getRound();
    Subscribe subscribe = round.getSubscribe();
    Member member = subscribe.getMember();

    NotificationCreateDTO dto = new NotificationCreateDTO();
    dto.setMemberId(member.getId());
    dto.setType(NotificationType.SUBSCRIBE_ROUND);
    String productName = subscribe.getOrderItem().getProduct().getName();
    Long subscribeId = subscribe.getId();
    int roundNo = round.getRoundNo();
    Long roundId = round.getId();

    switch (event.getPhase()) {
      case "DUE" -> {
        dto.setTitle("구독 결제일 안내");
        dto.setMessage(String.format("[%s] %d회차 결제일이 도래했습니다. 결제를 진행해주세요.",
                productName, roundNo));
        dto.setLinkUrl(String.format("/mypage/subscribe/round/%d", roundId));
      }

      case "WARNING" -> {
        dto.setTitle("결제 유예기간 만료 예정");
        dto.setMessage(String.format("[%s] %d회차 결제 유예기간이 내일 만료됩니다. 결제를 완료해주세요.",
                productName, roundNo));
        dto.setLinkUrl(String.format("/mypage/subscribe/%d/rounds/%d", subscribeId, roundNo));
      }

      case "OVERDUE" -> {
        dto.setTitle("연체 확정 - 서비스 중단");
        dto.setMessage(String.format("[%s] %d회차 결제가 유예기간을 초과했습니다. 구독이 중단되었습니다.",
                productName, roundNo));
        dto.setLinkUrl(String.format("/mypage/subscribe/%d/rounds/%d", subscribeId, roundNo));
      }
    }

    notificationService.send(dto);

    log.info(
            "[EVENT] phase={} 결제 예정 알림 생성 완료 → memberId={}, subscribeId={}, roundId={}, roundNo={}",
            event.getPhase(),
            member.getId(),
            subscribeId,
            roundId,
            roundNo
    );
  }
}
