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
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundCompletedEvent;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundRequestEvent;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
  @EventListener
  public void onPaymentStatusChanged(PaymentStatusChangedEvent event) {
    if (event.getTransition().getStatus() != PaymentStatus.PAID) return;

    paymentRepository.findById(event.getPaymentId()).ifPresent(payment -> {
      Long memberId = payment.getMember().getId();
      OrderPlanType planType = payment.getOrderItem().getOrderPlan().getType();

      NotificationCreateDTO dto = new NotificationCreateDTO();
      dto.setMemberId(memberId);
      dto.setType(NotificationType.PAYMENT);

      if (planType == OrderPlanType.RENTAL && payment.getSubscribeRound() != null) {
        // 구독 회차 결제 완료
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
        dto.setLinkUrl(String.format("/mypage/payment/%d", payment.getId()));
        notificationService.send(dto);
      }
    });
  }

  // 환불 요청 알림
  @EventListener
  public void onRefundApproved(RefundRequestEvent event) {
    Long orderItemId = event.getOrderItemId();
    Long subscribeId = event.getSubscribeId();
    RefundRequestStatus status = event.getStatus();

    NotificationCreateDTO dto = new NotificationCreateDTO();
    dto.setType(NotificationType.PAYMENT);

    if (subscribeId == null) { // 일반 결제 환불
      orderItemRepository.findById(orderItemId).ifPresent(orderItem -> {
        Long memberId = orderItem.getOrder().getMember().getId();
        String productName = orderItem.getProduct().getName();

        dto.setMemberId(memberId);
        dto.setTargetId(orderItem.getId());
        dto.setLinkUrl(String.format("/mypage/payment/%d", orderItem.getId()));

        switch (status) {
          case PENDING -> dto.setTitle("환불 요청 접수");
          case APPROVED, APPROVED_WAITING_RETURN -> dto.setTitle("환불 승인");
          case REJECTED -> dto.setTitle("환불 거절");
          case CANCELED -> dto.setTitle("환불 요청 취소");
          default -> { return; }
        }

        dto.setMessage("상품 [" + productName + "]의 환불 상태가 변경되었습니다.");
        notificationService.send(dto);
      });
    } else { // 구독 환불
      subscribeRepository.findById(subscribeId).ifPresent(subscribe -> {
        Long memberId = subscribe.getMember().getId();
        dto.setMemberId(memberId);
        dto.setTargetId(subscribe.getId());
        dto.setLinkUrl(String.format("/mypage/subscribe/%d", subscribe.getId()));

        switch (status) {
          case PENDING -> dto.setTitle("구독 환불 요청");
          case APPROVED, APPROVED_WAITING_RETURN -> dto.setTitle("구독 환불 승인");
          case REJECTED -> dto.setTitle("구독 환불 거절");
          case CANCELED -> dto.setTitle("구독 환불 요청 취소");
          default -> { return; }
        }

        dto.setMessage("구독 환불 상태가 변경되었습니다.");
        notificationService.send(dto);
      });
    }
  }

  // 환불 완료 알림
  @EventListener
  public void onRefundCompleted(RefundCompletedEvent event) {
    orderItemRepository.findById(event.getOrderItemId()).ifPresent(orderItem -> {
      Long memberId = orderItem.getOrder().getMember().getId();
      OrderPlanType planType = orderItem.getOrderPlan().getType();

      NotificationCreateDTO dto = new NotificationCreateDTO();
      dto.setMemberId(memberId);
      dto.setType(NotificationType.PAYMENT);

      if (event.getSubscribeId() != null && planType == OrderPlanType.RENTAL) {
        dto.setTitle("구독 환불 완료");
        dto.setMessage("구독 #" + event.getSubscribeId() + " 환불이 완료되었습니다.");
        dto.setTargetId(event.getSubscribeId());
        dto.setLinkUrl(String.format("/mypage/subscribe/%d", event.getSubscribeId()));
      } else {
        dto.setTitle("결제 환불 완료");
        dto.setMessage("상품 [" + orderItem.getProduct().getName() + "]의 환불이 완료되었습니다.");
        dto.setTargetId(orderItem.getId());
        dto.setLinkUrl(String.format("/mypage/payment/%d", orderItem.getId()));
      }

      notificationService.send(dto);
    });
  }

  // 구독 상태 변경 알림
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @EventListener
  public void onSubscribeStatusChanged(SubscribeStatusChangedEvent event) {
    subscribeRepository.findById(event.getSubscribeId()).ifPresent(subscribe -> {
      SubscribeStatus newStatus = event.getTransition().getStatus();
      SubscribeStatus currentStatus = subscribe.getStatus();
      if (newStatus == currentStatus) return;

      NotificationCreateDTO dto = new NotificationCreateDTO();
      dto.setMemberId(subscribe.getMember().getId());
      dto.setType(NotificationType.SUBSCRIBE);
      dto.setTitle("구독 상태 변경");
      dto.setMessage("구독이 " + newStatus.name() + " 상태로 변경되었습니다.");
      dto.setTargetId(subscribe.getId());
      dto.setLinkUrl(String.format("/mypage/subscribe/%d", subscribe.getId()));
      notificationService.send(dto);
    });
  }

  // 배송 상태 변경 알림
  @EventListener
  public void onDeliveryStatusChanged(DeliveryStatusChangedEvent event) {
    DeliveryStatus status = event.getStatus();
    if (status != DeliveryStatus.DELIVERED && status != DeliveryStatus.RETURNED) return;

    orderItemRepository.findById(event.getOrderItemId()).ifPresent(orderItem -> {
      Long memberId = orderItem.getOrder().getMember().getId();

      NotificationCreateDTO dto = new NotificationCreateDTO();
      dto.setMemberId(memberId);
      dto.setType(NotificationType.DELIVERY);
      dto.setTargetId(orderItem.getId());
      dto.setLinkUrl("/mypage/payment"); // 결제 내역 전체로 이동

      if (status == DeliveryStatus.DELIVERED) {
        dto.setTitle("배송 완료");
        dto.setMessage("상품이 배송 완료되었습니다.");
      } else {
        dto.setTitle("회수 완료");
        dto.setMessage("상품 회수가 완료되었습니다. 환불이 진행 중입니다.");
      }

      notificationService.send(dto);
    });
  }
}
