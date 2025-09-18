package com.imchobo.sayren_back.domain.payment.mapper;


import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.payment.dto.PaymentHistoryResponseDTO;
import com.imchobo.sayren_back.domain.payment.entity.PaymentHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface PaymentHistoryMapper {

  @Mapping(source = "id", target = "historyId")
  @Mapping(source = "payment", target = "paymentId", qualifiedByName = "mapPaymentId")
  PaymentHistoryResponseDTO toResponse(PaymentHistory history);

  List<PaymentHistoryResponseDTO> toResponseList(List<PaymentHistory> histories);
}
