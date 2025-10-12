package com.imchobo.sayren_back.domain.payment.mapper;


import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface PaymentMapper {

  // RequestDTO → Entity (등록/저장 시 사용)
  @Mapping(source = "orderItemId", target = "orderItem", qualifiedByName = "mapOrderItem")
  @Mapping(target = "amount", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "impUid", ignore = true)
  @Mapping(target = "merchantUid", ignore = true)
  @Mapping(target = "subscribeRound", ignore = true)
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "receipt", ignore = true)
  Payment toEntity(PaymentRequestDTO dto);

  // entity -> responseDRO(상세 응답)
  @Mapping(source = "id", target = "paymentId")
  @Mapping(source = "orderItem", target = "orderItemId", qualifiedByName = "mapOrderItemId")
  @Mapping(source = "merchantUid", target = "merchantUid")
  @Mapping(source = "impUid", target = "impUid")
  @Mapping(source = "amount", target = "amount")
  @Mapping(source = "paymentType", target = "paymentType")
  @Mapping(source = "paymentStatus", target = "paymentStatus")
  @Mapping(source = "receipt", target = "receiptUrl")
  @Mapping(source = "regDate", target = "regDate")
  @Mapping(source = "voidDate", target = "voidDate")

  // 상품·주문
  @Mapping(source = "orderItem.product.name", target = "productName")
  @Mapping(source = "orderItem.productPriceSnapshot", target = "priceSnapshot")
  @Mapping(source = "orderItem.orderPlan.type", target = "orderPlanType")

  // 구독·회차 (nullable)
  @Mapping(source = "subscribeRound.subscribe", target = "subscribeId", qualifiedByName = "mapSubscribeId")
  @Mapping(source = "subscribeRound", target = "roundId", qualifiedByName = "mapSubscribeRoundId")
  @Mapping(source = "subscribeRound.roundNo", target = "roundNo")
  @Mapping(source = "subscribeRound.payStatus", target = "roundStatus")
  @Mapping(source = "subscribeRound.dueDate", target = "dueDate")
  @Mapping(source = "subscribeRound.paidDate", target = "paidDate")

  // 회원 (관리자 전용)
  @Mapping(source = "member.name", target = "memberName")
  @Mapping(source = "member.email", target = "memberEmail")

  // 환불 상태 (서비스에서 세팅)
  @Mapping(target = "refundStatus", ignore = true)
  PaymentResponseDTO toResponseDTO(Payment entity);

  //  Entity → SummaryDTO (목록 조회)
  @Mapping(source = "id", target = "paymentId")
  @Mapping(source = "orderItem", target = "orderItemId", qualifiedByName = "mapOrderItemId")
  @Mapping(source = "amount", target = "amount")
  @Mapping(source = "paymentStatus", target = "paymentStatus")
  @Mapping(source = "regDate", target = "regDate")
  @Mapping(source = "orderItem.product.name", target = "productName")
  @Mapping(source = "orderItem.productPriceSnapshot", target = "priceSnapshot")
  @Mapping(source = "orderItem.orderPlan.type", target = "orderPlanType")
  PaymentSummaryDTO toSummaryDTO(Payment entity);

  @Mapping(target = "subscribeRound", source = "round")
  @Mapping(target = "member", source = "subscribe.member")
  @Mapping(target = "orderItem", source = "subscribe.orderItem")
  @Mapping(target = "amount", source = "amount")
  @Mapping(target = "merchantUid", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "impUid", ignore = true)
  @Mapping(target = "receipt", ignore = true)
  @Mapping(target = "paymentType", ignore = true)
  @Mapping(target = "paymentStatus", ignore = true)
  Payment toEntityFromRound(SubscribeRound round);
  // 자동 결제(구독 회차) 전용


  // 리스트 매핑
  List<PaymentResponseDTO> toResponseDTOList(List<Payment> entities);
  List<PaymentSummaryDTO> toSummaryDTOList(List<Payment> entities);


}
