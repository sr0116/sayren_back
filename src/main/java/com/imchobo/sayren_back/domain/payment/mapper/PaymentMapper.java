package com.imchobo.sayren_back.domain.payment.mapper;



import com.imchobo.sayren_back.domain.exentity.Member;
import com.imchobo.sayren_back.domain.exentity.Order;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",  imports = {LocalDateTime.class})
public interface PaymentMapper {



  // DTO → 엔티티
  @Mapping(target = "paymentId", ignore = true)
  @Mapping(target = "payStatus", expression = "java(defaultStatus())")
  @Mapping(source = "orderId", target = "order")
  @Mapping(source = "memberId", target = "member")
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
  default Order mapOrder(Long orderId) {
    if (orderId == null) return null;
    Order order = new Order();
    order.setOrderId(orderId);
    return order;
  }

  default Member mapMember(Long memberId) {
    if (memberId == null) return null;
    Member member = new Member();
    member.setMemberId(memberId);
    return member;
  }

  default PaymentStatus defaultStatus() {
    return PaymentStatus.PENDING;
  }

  default LocalDateTime now() {
    return LocalDateTime.now();
  }
}
