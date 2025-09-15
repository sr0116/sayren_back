package com.imchobo.sayren_back.domain.payment.mapper;


import com.imchobo.sayren_back.domain.exentity.Member;
import com.imchobo.sayren_back.domain.exentity.Order;
import com.imchobo.sayren_back.domain.exentity.OrderItem;
import com.imchobo.sayren_back.domain.exentity.OrderPlan;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface PaymentMapper {


  // DTO → 엔티티
  @Mapping(target = "paymentId", ignore = true)
  @Mapping(target = "payStatus", expression = "java(defaultStatus())")
  @Mapping(source = "orderId", target = "order", qualifiedByName = "mapOrder")
  @Mapping(source = "memberId", target = "member", qualifiedByName = "mapMember")
  @Mapping(target = "receipt", ignore = true)
  @Mapping(target = "merchantUid", ignore = true)
  @Mapping(target = "impUid", ignore = true)
  Payment toEntity(PaymentRequestDTO dto);


  // 엔티티 → ResponseDTO
  @Mapping(source = "order.orderId", target = "orderId")
  @Mapping(source = "member.memberId", target = "memberId")
  @Mapping(source = "receipt", target = "receiptUrl")
  PaymentResponseDTO toResponseDTO(Payment entity);

  // 엔티티 → SummaryDTO
  @Mapping(source = "payStatus", target = "status")
  PaymentSummaryDTO toSummaryDTO(Payment entity);

  List<PaymentResponseDTO> toResponseDTOs(List<Payment> entities);

  List<PaymentSummaryDTO> toSummaryDTOs(List<Payment> entities);

  // ====== 헬퍼 메서드 ======
  @Named("mapOrder")
  default Order mapOrder(Long orderId) {
    if (orderId == null) return null;
    return Order.builder().orderId(orderId).build();
  }


  // 멤버는 나중에 삭제 예정
  @Named("mapMember")
  default Member mapMember(Long memberId) {
    if (memberId == null) return null;
    return Member.builder().memberId(memberId).build();
  }

  @Named("mapPlan")
  default OrderPlan mapPlan(Long planId) {
    if (planId == null) return null;
    return OrderPlan.builder().planId(planId).build();
  }

  default PaymentStatus defaultStatus() {
    return PaymentStatus.PENDING;
  }

}
