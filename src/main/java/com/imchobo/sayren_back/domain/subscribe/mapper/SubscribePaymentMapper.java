package com.imchobo.sayren_back.domain.subscribe.mapper;


import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribePaymentRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribePaymentResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribePayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface SubscribePaymentMapper {
  // 디티오를 엔티티
  @Mapping(target = "subscribePaymentId", ignore = true)
  @Mapping(source = "subscribeId", target = "subscribe")
  @Mapping(source = "paymentId", target = "payment")
  @Mapping(target = "payStatus", expression = "java(defaultStatus())")
// 초기값
  SubscribePayment toEntity(SubscribePaymentRequestDTO dto);

  default Payment map(Long paymentId) {
    if (paymentId == null) return null;
    Payment payment = new Payment();
    payment.setPaymentId(paymentId);
    return payment;
  }

  default Subscribe mapSubscribe(Long subscribeId) {


    if (subscribeId == null) return null;
    Subscribe subscribe = new Subscribe();
    subscribe.setSubscribeId(subscribeId);
    return subscribe;
  }

  //
  default Long map(Payment payment) {
    return (payment != null) ? payment.getPaymentId() : null;
  }

  default Long map(Subscribe subscribe) {
    return (subscribe != null) ? subscribe.getSubscribeId() : null;
  }

  // 함수 메서드로 빼서 엔티티 expression에 넣기
  default PaymentStatus defaultStatus() {
    return PaymentStatus.PENDING;
  }

  default LocalDateTime now() {
    return LocalDateTime.now();
  }

  // 엔티티를 디티오
  SubscribePaymentResponseDTO toDto(SubscribePayment entity);

  // 리스트 조회시 필요
  List<SubscribePaymentResponseDTO> toResponseDTOS(List<SubscribePayment> entities);


  List<SubscribePayment> toEntities(List<SubscribePaymentRequestDTO> dtos);


}
