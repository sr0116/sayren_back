package com.imchobo.sayren_back.domain.subscribe.mapper;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.exentity.Order;
import com.imchobo.sayren_back.domain.exentity.OrderItem;
import com.imchobo.sayren_back.domain.exentity.OrderPlan;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring",  uses = {MappingUtil.class}, imports = {LocalDate.class})
public interface SubscribeMapper {

  // 클라이언트 요청 -> 엔티티
  // 기본 상태 (서비스 로직에서 pending)
  @Mapping(source = "orderItemId", target = "orderItem", qualifiedByName = "mapOrderItem")
  @Mapping(target = "member", ignore = true)   // SecurityContext에서 주입 예정
  @Mapping(target = "status", ignore = true)
  Subscribe toEntity(SubscribeRequestDTO dto);

  // 주문 + 주문 아이템 + 플랜 → 구독 신청 DTO
  @Mapping(source = "orderItem.id", target = "orderItemId")
  @Mapping(source = "orderItem.productPriceSnapshot", target = "monthlyFeeSnapshot")
  @Mapping(source = "plan.month", target = "totalMonths")
  @Mapping(target = "depositSnapshot", constant = "0")
  @Mapping(target = "startDate", expression = "java(LocalDate.now())")
  @Mapping(target = "endDate", expression = "java(LocalDate.now().plusMonths(plan.getMonth()))")
  SubscribeRequestDTO toRequestDTO(OrderItem orderItem, Order order, OrderPlan plan);

  // 응답 DTO
  @Mapping(source = "orderItem.id", target = "orderItemId")
  SubscribeResponseDTO toResponseDTO(Subscribe entity);
  // 리스트 변환
  List<SubscribeResponseDTO> toResponseDTOList(List<Subscribe> entity);

  // 마이페이지 같은 곳에서 간단 현황만 보여줄 때 사용(사용 미정)
  @Mapping(source = "status", target = "payStatus")
  SubscribeSummaryDTO toSummaryDTO(Subscribe entity);
  List<SubscribeSummaryDTO> toSummaryDTOList(List<Subscribe> entities);

}
