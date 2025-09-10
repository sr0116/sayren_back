package com.imchobo.sayren_back.domain.payment.mapper;



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


  @Mapping(target = "paymentId", ignore = true)
  @Mapping(target = "payStatus", expression = "java(defaultStatus())")
  @Mapping(source = "orderId", target = "order")
  Payment toEntity(PaymentRequestDTO dto);


  default Order map(Long orderId) {
    if (orderId == null) return null;
    Order order = new Order();
    order.setOrderId(orderId);
    return order;
  }

  default PaymentStatus defaultStatus() {
    return PaymentStatus.PENDING;
  }

  default LocalDateTime now() {
    return LocalDateTime.now();
  }

  // 결제 목록 조회시 DTO값으로 변환(요약값)
  PaymentSummaryDTO toSummaryDTO(Payment payment);

   List<PaymentResponseDTO> toResponseDTOs(List<Payment> entities);
   // 요약 리스트
   List<PaymentSummaryDTO> toSummaryDTOs(List<Payment> entities);
}
