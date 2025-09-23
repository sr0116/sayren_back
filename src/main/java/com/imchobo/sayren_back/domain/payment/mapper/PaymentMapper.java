package com.imchobo.sayren_back.domain.payment.mapper;


import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface PaymentMapper {

  // RequestDTO → Entity (등록/저장 시 사용)
  @Mapping(source = "orderItemId", target = "orderItem", qualifiedByName = "mapOrderItem")
  @Mapping(target = "amount", ignore = true)
  Payment toEntity(PaymentRequestDTO dto);

  // entity -> responseDRO(상세 응답)

  @Mapping(source = "id", target = "paymentId")
  @Mapping(source = "orderItem", target = "orderItemId", qualifiedByName = "mapOrderItemId")
  @Mapping(source = "receipt", target = "receiptUrl")
  PaymentResponseDTO  toResponseDTO(Payment entity);

  //  Entity → SummaryDTO (목록 조회)
  @Mapping(source = "id", target = "paymentId")
  PaymentSummaryDTO toSummaryDTO(Payment entity);

  // 리스트 매핑
  List<PaymentResponseDTO> toResponseDTOList(List<Payment> entities);
  List<PaymentSummaryDTO> toSummaryDTOList(List<Payment> entities);


}
