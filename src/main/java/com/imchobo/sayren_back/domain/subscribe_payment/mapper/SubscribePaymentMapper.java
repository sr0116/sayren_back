package com.imchobo.sayren_back.domain.subscribe_payment.mapper;


import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.subscribe_payment.dto.SubscribePaymentRequestDTO;
import com.imchobo.sayren_back.domain.subscribe_payment.dto.SubscribePaymentResponseDTO;
import com.imchobo.sayren_back.domain.subscribe_payment.entity.SubscribePayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtil.class},  imports = {LocalDateTime.class})
public interface SubscribePaymentMapper {
  // 디티오를 엔티티
  @Mapping(target = "id", ignore = true)
  @Mapping(source = "subscribeId", target = "subscribe", qualifiedByName = "mapSubscribe")
  @Mapping(source = "paymentId", target = "payment", qualifiedByName = "mapPayment")
  @Mapping(target = "payStatus", constant = "PENDING")
  SubscribePayment toEntity(SubscribePaymentRequestDTO dto);

  // 엔티티를 디티오
  @Mapping(source = "id", target = "subscribePaymentId")
  @Mapping(source = "subscribe.id", target = "subscribeId")
  @Mapping(source = "payment.id", target = "paymentId")
  SubscribePaymentResponseDTO toDto(SubscribePayment entity);

  // 리스트 조회시 필요
  List<SubscribePaymentResponseDTO> toResponseDTOS(List<SubscribePayment> entities);
  List<SubscribePayment> toEntities(List<SubscribePaymentRequestDTO> dtos);

}
