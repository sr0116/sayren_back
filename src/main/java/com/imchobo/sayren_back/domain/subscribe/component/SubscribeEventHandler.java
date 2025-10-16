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
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeNotFoundException;
import com.imchobo.sayren_back.domain.subscribe.exception.subscribe_round.SubscribeRoundNotFoundException;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeHistoryRepository;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
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

  // 최초 구독 생성 시 기록
  public void recordInit(Subscribe subscribe) {
    SubscribeHistory history = SubscribeHistory.builder()
            .subscribe(subscribe)
            .status(subscribe.getStatus()) // PENDING_PAYMENT
            .reasonCode(ReasonCode.NONE)
            .build();
    subscribeHistoryRepository.save(history);
  }

  //  구독 상태 변경 (히스토리 이벤트 핸들러) - 비동기 로그 기록
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
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
    SubscribeTransition transition = event.getTransition();
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

    // 회차 상태 처리 저장 (회차 이벤트 처리 사용)
    switch (transition) {
      case RETURNED_AND_CANCELED, ADMIN_FORCE_END -> {
        // 회차 상태 변경은 RefundEventHandler가 PaymentStatus 기반으로 처리하므로 여기서는 구독 상태만 기록
        // 미납·연체 회차를 직접 CANCELED 처리하도록 로직 추가
          log.info("[ROUND] 구독 종료/취소 감지 → 회차 상태 변경 수행 (subscribeId={})", subscribe.getId());

          // 모든 회차 조회 후, 결제 완료된 회차를 제외하고 상태 변경
          List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());

          for (SubscribeRound round : rounds) {
            PaymentStatus payStatus = round.getPayStatus();

            //  미납·실패·기존 취소·부분환불 회차만 CANCELED 처리
            if (payStatus == PaymentStatus.PENDING
                    || payStatus == PaymentStatus.FAILED
                    || payStatus == PaymentStatus.CANCELED
                    || payStatus == PaymentStatus.PARTIAL_REFUNDED) {
              round.setPayStatus(PaymentStatus.CANCELED);
              round.setFailedAt(LocalDateTime.now());
            }
          }

          // 회차 일괄 저장 추가
          subscribeRoundRepository.saveAll(rounds);
          log.info("[ROUND] 구독 종료/취소 → 미납/연체 회차 CANCELED 처리 완료 (총 {}건)", rounds.size());
        }

        case FAIL_PAYMENT -> {
        if (subscribe.getStatus() == SubscribeStatus.CANCELED
                || subscribe.getStatus() == SubscribeStatus.ENDED) {
          log.info("[SKIP] 이미 CANCELED 또는 ENDED 상태이므로 FAILED 처리 생략 → subscribeId={}", subscribe.getId());
          return;
        }

        List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
        rounds.forEach(r -> subscribeStatusChanger.changeSubscribeRound(r, SubscribeRoundTransition.PAY_FAIL));
        log.info("[ROUND] 결제 실패 → 전체 회차 FAILED 처리 완료 - subscribeId={}", subscribe.getId());
      }

      default -> log.debug("[ROUND] 구독 상태 {}는 회차 갱신 불필요", transition);
    }
  }

  // 결제 상태 변경 → 구독 회차 연동
  @Transactional
  @EventListener
  public void handlePaymentStatusChanged(PaymentStatusChangedEvent event) {
    log.info("[EVENT] handlePaymentStatusChanged → paymentId={}, transition={}", event.getPaymentId(), event.getTransition());

    // 해당 회차 조회
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

              // 상태 전환 switch 조건 처리
              switch (transition) {
                case PAY_SUCCESS, RETRY_SUCCESS -> {
                  round.setPayStatus(transition.getStatus());
                  round.setPaidDate(LocalDateTime.now());
                  round.setFailedAt(null);
                  round.setGracePeriodEndAt(null);
                  subscribeRoundRepository.save(round);
                  // 배송 상태 확인 후에
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
                // 결제 실패 (1회차 , n회차 분기 처리)
                case PAY_FAIL, RETRY_FAIL -> {
                  // 유예 기간 및 해당 기간 내 결제 실패시
                  round.setPayStatus(PaymentStatus.PENDING);
                  round.setFailedAt(LocalDateTime.now());
                  // 유예 기간은 실 납부일 결제 이후 3일까지 가능하게
                  LocalDate dueDate = round.getDueDate();
                  if (dueDate != null) {
                    round.setGracePeriodEndAt(dueDate.plusDays(3).atStartOfDay());
                  } else {
                    // dueDate가 없을 경우 현재 시점 기준 +3
                    round.setGracePeriodEndAt(LocalDateTime.now().plusDays(3));
                  }

                  // 예정일 이전이라면 연체 X → 단순 실패 알림만
                  if (dueDate != null && LocalDate.now().isBefore(dueDate)) {
                    NotificationCreateDTO earlyFailDto = new NotificationCreateDTO();
                    earlyFailDto.setMemberId(subscribe.getMember().getId());
                    earlyFailDto.setType(NotificationType.SUBSCRIBE);
                    earlyFailDto.setTitle("결제 실패");
                    earlyFailDto.setMessage(String.format("[%s] %d회차 결제가 실패했습니다. 예정일(%s) 전까지 다시 결제할 수 있습니다.",
                            subscribe.getOrderItem().getProduct().getName(),
                            round.getRoundNo(),
                            dueDate));
                    earlyFailDto.setLinkUrl(String.format("/mypage/subscribe/%d?round=%d",
                            subscribe.getId(), round.getRoundNo()));
                    earlyFailDto.setTargetId(round.getId());
                    notificationService.send(earlyFailDto);
                    log.info("예정일 이전 결제 실패 알림 전송 완료 → memberId={}, subscribeId={}, roundNo={}",
                            subscribe.getMember().getId(), subscribe.getId(), round.getRoundNo());
                  }
                  // 예정일이 null이거나 (즉, 아직 설정 안됨) 혹은 이미 지난 경우 → 유예기간 부여
                  else {
                    NotificationCreateDTO overdueDto = new NotificationCreateDTO();
                    overdueDto.setMemberId(subscribe.getMember().getId());
                    overdueDto.setType(NotificationType.SUBSCRIBE);
                    overdueDto.setTitle("결제 지연 - 유예기간 시작");
                    overdueDto.setMessage(String.format("[%s] %d회차 결제가 미납 상태입니다. 결제일 이후 3일 내에 결제하지 않으면 서비스가 중단됩니다.",
                            subscribe.getOrderItem().getProduct().getName(),
                            round.getRoundNo()));
                    overdueDto.setLinkUrl(String.format("/mypage/subscribe/%d?round=%d",
                            subscribe.getId(), round.getRoundNo()));
                    overdueDto.setTargetId(round.getId());
                    notificationService.send(overdueDto);
                    log.info("유예기간 시작 알림 전송 완료 → memberId={}, subscribeId={}, roundNo={}",
                            subscribe.getMember().getId(), subscribe.getId(), round.getRoundNo());
                  }

                  // 회차별 상태 저장 및 구독 전체 실패 처리
                  if (round.getRoundNo() == 1) {
                    failAllRounds(subscribe, transition);
                    subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.FAIL_PAYMENT, ActorType.SYSTEM);
                    log.info("구독 [{}] 1회차 결제 실패 → 전체 FAILED", subscribe.getId());
                  } else {
                    subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.OVERDUE_PENDING, ActorType.SYSTEM);
                    log.info("구독 [{}] {}회차 결제 실패 → 유예기간 3일 시작", subscribe.getId(), round.getRoundNo());
                  }
                }

                // 결제 유예기간 초과
                case PAY_TIMEOUT -> {
                  // 스케줄러에서 처리 (주석)
//                  round.setPayStatus(transition.getStatus());
//                  subscribeRoundRepository.save(round);
//                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.OVERDUE_FINAL, ActorType.SYSTEM);
//                  log.info("구독 [{}] 결제 타임아웃 → OVERDUE_FINAL", subscribe.getId());
                }
                // 1회차 결제 실패
                case INIT_FAIL -> {
                  failAllRounds(subscribe, transition);
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.FAIL_PAYMENT, ActorType.SYSTEM);
                  log.info("구독 [{}] INIT_FAIL → 전체 FAILED", subscribe.getId());
                }

                // 전체 환불 / 취소 처리 (보증금 포함 전체 환불)
                case CANCEL_ALL, CANCEL_FUTURE_ONLY -> {
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.RETURNED_AND_CANCELED, ActorType.SYSTEM);
                  log.info("구독 [{}] 환불 요청 감지 → RETURNED_AND_CANCELED 전환 (회차 변경은 RefundEventHandler에서 처리)", subscribe.getId());
                }

//                // 전체 환불 / 취소 처리 (보증금 포함 전체 환불)
//                case CANCEL_ALL -> {
//                  cancelAllRounds(subscribe, transition);
//                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.RETURNED_AND_CANCELED, ActorType.SYSTEM);
//                  log.info("구독 [{}] 전체 CANCEL 처리 (보증금 포함 모든 회차 REFUNDED)", subscribe.getId());
//                }

//                // 보증금 기준 부분 환불 (미래 회차만 취소)
//                case CANCEL_FUTURE_ONLY -> {
//                  List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
//
//                  //  오늘 이후 회차만 CANCELED 처리, 이미 납부된 회차는 유지
//                  for (SubscribeRound r : rounds) {
//                    if (r.getDueDate() != null && r.getDueDate().isAfter(LocalDate.now())
//                            && r.getPayStatus() != PaymentStatus.PAID) {
//                      r.setPayStatus(PaymentStatus.CANCELED);
//                      subscribeRoundRepository.save(r);
//                      log.debug("회차 {} 취소 처리 완료 (dueDate={}, status=CANCELED)", r.getRoundNo(), r.getDueDate());
//                    }
//                  }
//                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.RETURNED_AND_CANCELED, ActorType.SYSTEM);
//                  log.info("구독 [{}] CANCEL_FUTURE_ONLY → 미래 회차만 CANCELED, 보증금 환불 완료", subscribe.getId());
//                }
                // 강제 종료
                  case FORCED_END -> {
                  failAllRounds(subscribe, transition);
                  subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.OVERDUE_FINAL, ActorType.SYSTEM);
                  log.info("구독 [{}] FORCED_END → 전체 FAILED", subscribe.getId());
                }
                default -> log.debug("기타 상태 전환 생략 → transition={}", transition);
              }
            });
  }

  // 헬퍼 메서드
  // 모든 회차 상태 변경 (fail)
  private void failAllRounds(Subscribe subscribe, SubscribeRoundTransition transition) {
    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
    LocalDateTime now = LocalDateTime.now();

    rounds.forEach(r -> {
      r.setPayStatus(transition.getStatus());
      r.setFailedAt(now);
    });
    subscribeRoundRepository.saveAll(rounds);
  }

  // 모든 회차 상태 변경 (refunded)
  private void cancelAllRounds(Subscribe subscribe, SubscribeRoundTransition transition) {
    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
    rounds.forEach(r -> subscribeStatusChanger.changeSubscribeRound(r, transition)); // 이벤트 처리로 변겨ㅛㅇ
    subscribeRoundRepository.saveAll(rounds);
  }

  // 결제 이벤트 처리 -> 회차 이벤트 연결
  private SubscribeRoundTransition mapToRoundTransition(PaymentTransition transition) {
    if (transition == null) return null;
    return switch (transition) {
      case COMPLETE -> SubscribeRoundTransition.PAY_SUCCESS;
      case FAIL_USER, FAIL_PAYMENT, FAIL_SYSTEM -> SubscribeRoundTransition.PAY_FAIL;

      case FAIL_TIMEOUT -> SubscribeRoundTransition.PAY_TIMEOUT;
      case REFUND, PARTIAL_REFUND, CANCEL_FUTURE_ONLY -> SubscribeRoundTransition.CANCEL; // 환불 관련 이벤트 무시
      default -> null;
    };
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
    dto.setType(NotificationType.SUBSCRIBE);

    switch (event.getPhase()) {
      case "DUE" -> {
        dto.setTitle("구독 결제일 안내");
        dto.setMessage(String.format("[%s] %d회차 결제일이 도래했습니다. 결제를 진행해주세요.",
                subscribe.getOrderItem().getProduct().getName(), round.getRoundNo()));
        dto.setLinkUrl(String.format("/mypage/subscribe/round/%d?autoPay=true", round.getId()));
      }
      case "WARNING" -> {
        dto.setTitle("결제 유예기간 만료 예정");
        dto.setMessage(String.format("[%s] %d회차 결제 유예기간이 내일 만료됩니다. 결제를 완료해주세요.",
                subscribe.getOrderItem().getProduct().getName(), round.getRoundNo()));
        dto.setLinkUrl(String.format("/mypage/subscribe/%d", subscribe.getId()));
      }
      case "OVERDUE" -> {
        dto.setTitle("연체 확정 - 서비스 중단");
        dto.setMessage(String.format("[%s] %d회차 결제가 유예기간을 초과했습니다. 구독이 중단되었습니다.",
                subscribe.getOrderItem().getProduct().getName(), round.getRoundNo()));
        dto.setLinkUrl(String.format("/mypage/subscribe/%d", subscribe.getId()));
      }
    }

    notificationService.send(dto);
    log.info("[EVENT] phase={} 결제 예정 알림 생성 완료 → memberId={}, subscribeId={}, roundNo={}",
            event.getPhase(), member.getId(), subscribe.getId(), round.getRoundNo());
  }


  // 배송 완료시 이벤트 처리
  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void onDeliveryStatusChanged(DeliveryStatusChangedEvent event) {

    if (event.getStatus() != DeliveryStatus.DELIVERED) return;

    log.info("[EVENT] 배송 완료 감지 → orderItemId={}", event.getOrderItemId());

    subscribeRepository.findByOrderItem_Id(event.getOrderItemId())
            .ifPresentOrElse(subscribe -> {
              // 이미 ACTIVE라면 스킵
              if (subscribe.getStatus() == SubscribeStatus.ACTIVE) {
                log.info("[SKIP] 이미 ACTIVE 상태이므로 생략 → subscribeId={}", subscribe.getId());
                return;
              }

              // 구독 개월 수 계산
              int months = subscribe.getOrderItem().getOrderPlan().getMonth();
              LocalDate start = LocalDate.now();
              LocalDate end = start.plusMonths(months);

              // 시작일 / 종료일 확정
              subscribe.setStartDate(start);
              subscribe.setEndDate(end);

              // 상태 ACTIVE로 전환
              subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.START, ActorType.SYSTEM);

              log.info("[DELIVERY→SUBSCRIBE] 구독 기간 확정 및 ACTIVE 전환 완료 → start={}, end={}", start, end);

              // 활성화 이벤트 발행
              eventPublisher.publishEvent(new SubscribeActivatedEvent(subscribe.getId(), start));

            }, () -> log.warn("[DELIVERY→SUBSCRIBE] orderItemId={} 에 해당 구독 없음", event.getOrderItemId()));
  }
}
