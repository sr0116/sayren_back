package com.imchobo.sayren_back.domain.payment.portone.mapper;


import com.imchobo.sayren_back.domain.payment.portone.dto.CancelResponse;
import com.imchobo.sayren_back.domain.payment.portone.dto.PaymentInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface PortOneMapper {

  // Map → DTO 변환 (결제 조회 응답)
  @Mapping(target = "impUid", expression = "java(mapToString(response.get(\"imp_uid\")))")
  @Mapping(target = "merchantUid", expression = "java(mapToString(response.get(\"merchant_uid\")))")
  @Mapping(target = "amount", expression = "java(mapToLong(response.get(\"amount\")))")
  @Mapping(target = "status", expression = "java(mapToString(response.get(\"status\")))")
  PaymentInfoResponse toPaymentInfoResponse(Map<String, Object> response);

  // Map → DTO 변환 (환불 응답)
  @Mapping(target = "impUid", expression = "java(mapToString(response.get(\"imp_uid\")))")
  @Mapping(target = "merchantUid", expression = "java(mapToString(response.get(\"merchant_uid\")))")
  @Mapping(target = "amount", expression = "java(mapToLong(response.get(\"amount\")))")
  @Mapping(target = "reason", expression = "java(mapToString(response.get(\"reason\")))")
  CancelResponse toCancelResponse(Map<String, Object> response);

  // 리스트 변환 (여러 건 응답 시)
  List<PaymentInfoResponse> toPaymentInfoResponses(List<Map<String, Object>> responses);
  List<CancelResponse> toCancelResponses(List<Map<String, Object>> responses);

  // ====== 메서드 ======
  default String mapToString(Object value) {
    return value != null ? value.toString() : null;
  }

  default Long mapToLong(Object value) {
    if (value instanceof Number num) return num.longValue();
    if (value != null) return Long.parseLong(value.toString());
    return null;
  }
}
