package com.imchobo.sayren_back.domain.payment.portone.mapper;


import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.payment.portone.dto.cancel.CancelResponse;
import com.imchobo.sayren_back.domain.payment.portone.dto.payment.PaymentInfoResponse;
import com.imchobo.sayren_back.domain.payment.portone.dto.payment.PaymentVerifyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;


@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface PortOneMapper {

  // Map → DTO 변환 (결제 조회 응답)
  @Mapping(source = "imp_uid", target = "impUid", qualifiedByName = "toStringSafe")
  @Mapping(source = "merchant_uid", target = "merchantUid", qualifiedByName = "toStringSafe")
  @Mapping(source = "amount", target = "amount", qualifiedByName = "toLongSafe")
  @Mapping(source = "status", target = "status", qualifiedByName = "toStringSafe")
  @Mapping(source = "fail_reason", target = "failReason", qualifiedByName = "toStringSafe")
  @Mapping(source = "error_code", target = "errorCode", qualifiedByName = "toStringSafe")
  @Mapping(source = "receipt_url", target = "receiptUrl", qualifiedByName = "toStringSafe")
  PaymentInfoResponse toPaymentInfoResponse(Map<String, Object> response);

  // Map → DTO 변환 (결제 검증 응답)
  @Mapping(source = "imp_uid", target = "impUid", qualifiedByName = "toStringSafe")
  @Mapping(source = "merchant_uid", target = "merchantUid", qualifiedByName = "toStringSafe")
  @Mapping(source = "amount", target = "amount", qualifiedByName = "toLongSafe")
  @Mapping(source = "status", target = "status", qualifiedByName = "toStringSafe")
  @Mapping(source = "pay_method", target = "payMethod", qualifiedByName = "toStringSafe")
  @Mapping(source = "buyer_name", target = "buyerName", qualifiedByName = "toStringSafe")
  @Mapping(source = "buyer_email", target = "buyerEmail", qualifiedByName = "toStringSafe")
  @Mapping(source = "error_code", target = "errorCode", qualifiedByName = "toStringSafe")
  @Mapping(source = "error_msg", target = "errorMsg", qualifiedByName = "toStringSafe")
  PaymentVerifyResponse toPaymentVerifyResponse(Map<String, Object> response);

  // Map → DTO 변환 (환불 응답)
  @Mapping(source = "imp_uid", target = "impUid", qualifiedByName = "toStringSafe")
  @Mapping(source = "merchant_uid", target = "merchantUid", qualifiedByName = "toStringSafe")
  @Mapping(source = "amount", target = "amount", qualifiedByName = "toLongSafe")
  @Mapping(source = "reason", target = "reason", qualifiedByName = "toStringSafe")
  CancelResponse toCancelResponse(Map<String, Object> response);

  // 리스트 변환
  List<PaymentInfoResponse> toPaymentInfoResponses(List<Map<String, Object>> responses);
  List<PaymentVerifyResponse> toPaymentVerifyResponses(List<Map<String, Object>> responses);
  List<CancelResponse> toCancelResponses(List<Map<String, Object>> responses);
}
