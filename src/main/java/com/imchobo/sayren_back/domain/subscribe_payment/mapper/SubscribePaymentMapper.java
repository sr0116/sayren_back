package com.imchobo.sayren_back.domain.subscribe_payment.mapper;


import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe_payment.dto.SubscribePaymentRequestDTO;
import com.imchobo.sayren_back.domain.subscribe_payment.dto.SubscribePaymentResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe_payment.entity.SubscribePayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface SubscribePaymentMapper {
  // 디티오를 엔티티
  @Mapping(target = "subscribePaymentId", ignore = true)
  @Mapping(source = "subscribeId", target = "subscribe", qualifiedByName = "mapSubscribe")
  @Mapping(source = "paymentId", target = "payment", qualifiedByName = "mapPayment")
  @Mapping(target = "payStatus", expression = "java(defaultStatus())")
  SubscribePayment toEntity(SubscribePaymentRequestDTO dto);

  // 엔티티를 디티오
  @Mapping(source = "subscribe.subscribeId", target = "subscribeId")
  @Mapping(source = "payment.paymentId", target = "paymentId")
  SubscribePaymentResponseDTO toDto(SubscribePayment entity);

  // 리스트 조회시 필요
  List<SubscribePaymentResponseDTO> toResponseDTOS(List<SubscribePayment> entities);
  List<SubscribePayment> toEntities(List<SubscribePaymentRequestDTO> dtos);

  // 나중에 공통 헬퍼 메서드는 따로 빼서 사용
  //=================== 헬퍼 메서드 ================= /////
  @Named("mapPayment")
  default Payment mapPayment(Long paymentId) {
    if (paymentId == null) return null;
    Payment payment = new Payment();
    payment.setPaymentId(paymentId);
    return payment;
  }

  @Named("mapSubscribe")
  default Subscribe mapSubscribe(Long subscribeId) {
    if (subscribeId == null) return null;
    Subscribe subscribe = new Subscribe();
    subscribe.setSubscribeId(subscribeId);
    return subscribe;
  }
  
  // 함수 메서드로 빼서 엔티티 expression에 넣기
  default PaymentStatus defaultStatus() {
    return PaymentStatus.PENDING;
  }

}
