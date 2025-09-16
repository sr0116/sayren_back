package com.imchobo.sayren_back.domain.payment.mapper;


import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.exentity.Order;
import com.imchobo.sayren_back.domain.exentity.OrderPlan;
import com.imchobo.sayren_back.domain.member.entity.Member;
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

@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface PaymentMapper {


  // DTO → 엔티티
  @Mapping(target = "id", ignore = true)               // PK는 DB에서 자동 생성
  @Mapping(target = "member", ignore = true)           // member는 SecurityContext에서 주입
  @Mapping(source = "orderId", target = "order", qualifiedByName = "mapOrder")     // orderId → Order 엔티티 변환
  @Mapping(target = "merchantUid", ignore = true)
  @Mapping(target = "impUid", ignore = true)
  @Mapping(target = "receipt", ignore = true)
  @Mapping(target = "payStatus", constant = "PENDING")
  Payment toEntity(PaymentRequestDTO dto);

  // 엔티티 → ResponseDTO
  @Mapping(source = "receipt", target = "receiptUrl")
  PaymentResponseDTO toResponseDTO(Payment entity);

  List<PaymentResponseDTO> toResponseDTOs(List<Payment> entities);

  // 엔티티 → SummaryDTO
  @Mapping(source = "payStatus", target = "status")
  PaymentSummaryDTO toSummaryDTO(Payment entity);

  List<PaymentSummaryDTO> toSummaryDTOs(List<Payment> entities);

}
