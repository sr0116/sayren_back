package com.imchobo.sayren_back.domain.common.util;

import com.imchobo.sayren_back.domain.attach.entity.Attach;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.order.entity.Order;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class MappingUtil {
  // 필수 fk  → 엔티티 매핑 매서드
  @Named("mapPayment")
  public Payment paymentIdToEntity(Long paymentId) {
    if (paymentId == null) throw new IllegalArgumentException("paymentId가 null입니다.");
    return Payment.builder().id(paymentId).build();
  }

  @Named("mapOrder")
  public Order orderIdToEntity(Long orderId) {
    if (orderId == null) {
      throw new IllegalArgumentException("orderId가 null입니다.");
    }
    return Order.builder().id(orderId).build();
  }

  @Named("mapOrderItem")
  public OrderItem orderItemIdToEntity(Long orderItemId) {
    if (orderItemId == null) throw new IllegalArgumentException("orderItemId가 null입니다.");
    return OrderItem.builder().id(orderItemId).build();
  }

  @Named("mapSubscribe")
  public Subscribe subscribeIdToEntity(Long subscribeId) {
    if (subscribeId == null) throw new IllegalArgumentException("subscribeId가 null입니다.");
    return Subscribe.builder().id(subscribeId).build();
  }

  @Named("mapRefundRequest")
  public RefundRequest refundRequestIdToEntity(Long refundRequestId) {
    if (refundRequestId == null) return null; // 자동 환불일 경우 null 허용
    return RefundRequest.builder().id(refundRequestId).build();
  }

  @Named("mapRefundRequestId")
  public Long refundRequestEntityToId(RefundRequest refundRequest) {
    return refundRequest != null ? refundRequest.getId() : null;
  }


  @Named("mapOrderPlan")
  public OrderPlan orderPlanIdToEntity(Long planId) {
    return planId != null ? OrderPlan.builder().id(planId).build() : null;
  }

  @Named("mapAttachUrl")
  public static String mapAttachUrl(Attach attach) {
    if (attach == null) return null;

    return "https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
            + attach.getPath() + "/" + attach.getUuid();
  }
  //  엔티티 → ID 변환 (DTO 응답용)
  @Named("mapPaymentId")
  public Long paymentEntityToId(Payment payment) {
    return payment != null ? payment.getId() : null;
  }

  @Named("mapOrderId")
  public Long orderEntityToId(Order order) {
    return order != null ? order.getId() : null;
  }

  @Named("mapOrderItemId")
  public Long orderItemEntityToId(OrderItem orderItem) {
    return orderItem != null ? orderItem.getId() : null;
  }

  @Named("mapSubscribeId")
  public Long subscribeEntityToId(Subscribe subscribe) {
    return subscribe != null ? subscribe.getId() : null;
  }

  @Named("mapSubscribeRoundId")
  public Long subscribeRoundEntityToId(SubscribeRound subscribeRound) {
    return subscribeRound != null ? subscribeRound.getId() : null;
  }

  @Named("mapOrderPlanId")
  public Long orderPlanEntityToId(OrderPlan orderPlan) {
    return orderPlan != null ? orderPlan.getId() : null;
  }

  @Named("mapPaymentStatus")
  public PaymentStatus mapPaymentStatus(String status) {
    if (status == null) return null;
    try {
      return PaymentStatus.valueOf(status.toUpperCase()); // PortOne 응답이 소문자일 수도 있으니 보정
    } catch (IllegalArgumentException e) {
      return PaymentStatus.PENDING; // 기본값 or 예외처리
    }
  }


  // === 공통 변환 ===
  @Named("toStringSafe")
  public String toStringSafe(Object value) {
    return value != null ? value.toString() : null;
  }

  @Named("toLongSafe")
  public Long toLongSafe(Object value) {
    if (value instanceof Number num) return num.longValue();
    if (value != null) return Long.parseLong(value.toString());
    return null;
  }


            //배송 필요매핑 DeliveryMapper
    @Named("mapDeliveryTypeToString")
    public String mapDeliveryTypeToString(DeliveryType type) {
      return type != null ? type.name() : null;
    }

    @Named("mapDeliveryStatusToString")
    public String mapDeliveryStatusToString(DeliveryStatus status) {
      return status != null ? status.name() : null;
    }
  }



