package com.imchobo.sayren_back.domain.notification.component;

import com.imchobo.sayren_back.domain.delivery.component.event.DeliveryStatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.notification.dto.NotificationCreateDTO;
import com.imchobo.sayren_back.domain.notification.en.NotificationType;
import com.imchobo.sayren_back.domain.notification.service.NotificationService;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundApprovedEvent;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundCompletedEvent;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationEventHandler {

  private final NotificationService notificationService;
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final PaymentRepository paymentRepository;
  private final SubscribeRepository subscribeRepository;
  private final OrderItemRepository orderItemRepository;

  // 결제 완료 알림
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onPaymentStatusChanged(PaymentStatusChangedEvent event) {

    if (event.getTransition().getStatus() != PaymentStatus.PAID) {
      return;
    }

    paymentRepository.findById(event.getPaymentId()).ifPresent(payment -> {
      Long memberId = payment.getMember().getId();
      OrderPlanType planType = payment.getOrderItem().getOrderPlan().getType();

      NotificationCreateDTO dto = new NotificationCreateDTO();
      dto.setMemberId(memberId);
      dto.setType(NotificationType.PAYMENT);

      if (planType == OrderPlanType.RENTAL && payment.getSubscribeRound() != null) {
        // 구독 결제 완료
        subscribeRoundRepository.findById(payment.getSubscribeRound().getId()).ifPresent(round -> {
          dto.setTitle("구독 결제 완료");
          dto.setMessage(round.getRoundNo() + "회차 결제가 완료되었습니다.");
          dto.setTargetId(round.getId());
          dto.setLinkUrl(String.format("/mypage/subscribe/%d/rounds/%d",
                  round.getSubscribe().getId(), round.getRoundNo()));
          notificationService.send(dto);
        });
      } else if (planType == OrderPlanType.PURCHASE) {
        // 일반 결제 완료
        dto.setTitle("결제 완료");
        dto.setMessage("상품 [" + payment.getOrderItem().getProduct().getName() + "] 결제가 완료되었습니다.");
        dto.setTargetId(payment.getOrderItem().getId());
        dto.setLinkUrl("/mypage/order/" + payment.getOrderItem().getId());
        notificationService.send(dto);
      }
    });
  }

  @EventListener
  public void onRefundApproved(RefundApprovedEvent event) {
    Long subscribeId = event.getSubscribeId();

    NotificationCreateDTO dto = new NotificationCreateDTO();
    dto.setType(NotificationType.PAYMENT);

    // 일반 결제 환불 승인 (구독 ID가 없는 경우)
    if (subscribeId == null) {
      log.warn("구독 ID가 null → 일반 결제 환불 승인 알림으로 처리");

      orderItemRepository.findById(event.getOrderItemId()).ifPresent(orderItem -> {
        Long memberId = orderItem.getOrder().getMember().getId();
        dto.setMemberId(memberId);
        dto.setTitle("결제 환불 승인");
        dto.setMessage("상품 [" + orderItem.getProduct().getName() + "] 결제 환불이 승인되었습니다.");
        dto.setTargetId(orderItem.getId());
        dto.setLinkUrl("/mypage/order/" + orderItem.getId());

        notificationService.send(dto);
        log.info("일반 결제 환불 승인 알림 전송 완료 → memberId={}, orderItemId={}", memberId, orderItem.getId());
      });
    }
    // 구독 환불 승인 (구독 ID가 있는 경우)
    else {
      subscribeRepository.findById(subscribeId).ifPresent(subscribe -> {
        Long memberId = subscribe.getMember().getId();
        OrderPlanType planType = subscribe.getOrderItem().getOrderPlan().getType();

        dto.setMemberId(memberId);

        if (planType == OrderPlanType.RENTAL) {
          dto.setTitle("구독 환불 승인");
          dto.setMessage("구독 #" + subscribe.getId() + " 환불이 승인되었습니다.");
          dto.setTargetId(subscribe.getId());
          dto.setLinkUrl(String.format("/mypage/subscribe/%d", subscribe.getId()));
          notificationService.send(dto);
          log.info("구독 환불 승인 알림 전송 완료 → memberId={}, subscribeId={}", memberId, subscribe.getId());
        }
      });
    }
  }


  @EventListener
  public void onRefundCompleted(RefundCompletedEvent event) {
    orderItemRepository.findById(event.getOrderItemId()).ifPresent(orderItem -> {
      Long memberId = orderItem.getOrder().getMember().getId();
      OrderPlanType planType = orderItem.getOrderPlan().getType();

      NotificationCreateDTO dto = new NotificationCreateDTO();
      dto.setMemberId(memberId);
      dto.setType(NotificationType.PAYMENT);

      if (event.getSubscribeId() != null && planType == OrderPlanType.RENTAL) {
        // 구독 환불
        dto.setTitle("구독 환불 완료");
        dto.setMessage("구독 #" + event.getSubscribeId() + " 환불이 완료되었습니다. 영업일 기준 1~3일 내 입금됩니다.");
        dto.setTargetId(event.getSubscribeId());
        dto.setLinkUrl(String.format("/mypage/subscribe/%d", event.getSubscribeId()));
      } else {
        // 일반 결제 환불
        dto.setTitle("결제 환불 완료");
        dto.setMessage("상품 [" + orderItem.getProduct().getName() + "]의 환불이 완료되었습니다.");
        dto.setTargetId(orderItem.getId());
        dto.setLinkUrl("/mypage/order/" + orderItem.getId());
      }

      notificationService.send(dto);

      log.info("환불 완료 알림 전송 → memberId={}, orderItemId={}, subscribeId={}, planType={}",
              memberId, orderItem.getId(), event.getSubscribeId(), planType);
    });
  }

  // 구독 상태 변경 알림
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onSubscribeStatusChanged(SubscribeStatusChangedEvent event) {
    subscribeRepository.findById(event.getSubscribeId()).ifPresent(subscribe -> {
      Long memberId = subscribe.getMember().getId();

      NotificationCreateDTO dto = new NotificationCreateDTO();
      dto.setMemberId(memberId);
      dto.setType(NotificationType.SUBSCRIBE);
      dto.setTitle("구독 상태 변경");
      dto.setMessage("구독이 " + event.getTransition().getStatus().name() + " 상태로 변경되었습니다.");
      dto.setTargetId(subscribe.getId());
      dto.setLinkUrl(String.format("/mypage/subscribe/%d", subscribe.getId()));


      notificationService.send(dto);

      log.info("구독 상태 변경 알림 생성 → memberId={}, subscribeId={}, status={}",
              memberId, subscribe.getId(), event.getTransition().getStatus());
    });
  }

  // 배송 상태 변경 → 도착 및 회수 완료 알림
  @EventListener
  public void onDeliveryStatusChanged(DeliveryStatusChangedEvent event) {
    log.info(" [Listener Triggered] DeliveryStatusChangedEvent 수신됨 → {}", event.getStatus());
    DeliveryStatus status = event.getStatus();

    // 도착 또는 회수 완료 외 상태는 무시
    if (status != DeliveryStatus.DELIVERED && status != DeliveryStatus.RETURNED) {
      return;
    }

    subscribeRepository.findByOrderItem_Id(event.getOrderItemId()).ifPresent(subscribe -> {
      Long memberId = subscribe.getMember().getId();
      OrderPlanType planType = subscribe.getOrderItem().getOrderPlan().getType();

      NotificationCreateDTO dto = new NotificationCreateDTO();
      dto.setMemberId(memberId);
      dto.setType(NotificationType.DELIVERY);
      dto.setTargetId(subscribe.getId());

      if (status == DeliveryStatus.DELIVERED) {
        // 배송 완료
        if (planType == OrderPlanType.RENTAL) {
          dto.setTitle("구독 상품 배송 완료");
          dto.setMessage("구독 상품이 배송 완료되었습니다. 구독이 시작됩니다.");
          dto.setLinkUrl("/mypage/subscribe/" + subscribe.getId());
        } else {
          dto.setTitle("상품 배송 완료");
          dto.setMessage("구매하신 상품이 배송 완료되었습니다.");
          dto.setLinkUrl("/mypage/order/" + event.getOrderItemId());
        }
        notificationService.send(dto);

        log.info("배송 완료 알림 전송 → memberId={}, orderItemId={}, planType={}",
                memberId, event.getOrderItemId(), planType);

      } else if (status == DeliveryStatus.RETURNED) {
        // 회수 완료
        if (planType == OrderPlanType.RENTAL) {
          dto.setTitle("구독 회수 완료");
          dto.setMessage("구독 상품 회수가 완료되었습니다. 환불이 진행 중입니다.");
          dto.setLinkUrl("/mypage/subscribe/" + subscribe.getId());
        } else {
          dto.setTitle("상품 회수 완료");
          dto.setMessage("반품하신 상품의 회수가 완료되었습니다. 환불이 진행 중입니다.");
          dto.setLinkUrl("/mypage/order/" + event.getOrderItemId());
        }
        notificationService.send(dto);

        log.info("회수 완료 알림 전송 → memberId={}, orderItemId={}, planType={}",
                memberId, event.getOrderItemId(), planType);
      }
    });
  }

}
