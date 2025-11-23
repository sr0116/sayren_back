package com.imchobo.sayren_back.domain.subscribe.mapper;


import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.order.entity.Order;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring",  uses = {MappingUtil.class}, imports = {LocalDate.class})
public interface SubscribeMapper {



  // 클라이언트 요청 -> 엔티티
  // 기본 상태 (서비스 로직에서 pending)
  @Mapping(source = "orderItemId", target = "orderItem", qualifiedByName = "mapOrderItem")
  @Mapping(target = "member", ignore = true)   // SecurityContext에서 주입 예정
  Subscribe toEntity(SubscribeRequestDTO dto);

  // 주문 + 주문 아이템 + 플랜 → 구독 신청 DTO
  @Mapping(source = "orderItem.id", target = "orderItemId")
  @Mapping(source = "orderItem.productPriceSnapshot", target = "monthlyFeeSnapshot")
  @Mapping(source = "plan.month", target = "totalMonths")
  SubscribeRequestDTO toRequestDTO(OrderItem orderItem, Order order, OrderPlan plan);

  // 응답 DTO (Subscribe → SubscribeResponseDTO)
  @Mapping(source = "id", target = "subscribeId")
  @Mapping(source = "orderItem.id", target = "orderItemId")
  @Mapping(source = "orderItem.orderPlan.month", target = "totalMonths")
  @Mapping(source = "member.name", target = "memberName")
  @Mapping(source = "member.email", target = "memberEmail")
  @Mapping(source = "orderItem.product.name", target = "productName")
  @Mapping(source = "orderItem.product.productCategory", target = "productCategory")
  @Mapping(source = "orderItem.product", target = "productThumbnail",
          qualifiedByName = "mapProductThumbnailUrl")
  SubscribeResponseDTO toResponseDTO(Subscribe entity);


  List<SubscribeResponseDTO> toResponseDTOList(List<Subscribe> entity);

  // 요약 DTO
  @Mapping(source = "id", target = "subscribeId")
  @Mapping(source = "monthlyFeeSnapshot", target = "monthlyFeeSnapshot")
  @Mapping(source = "orderItem.product.name", target = "productName")
  @Mapping(source = "orderItem.product", target = "productThumbnail",
          qualifiedByName = "mapProductThumbnailUrl")
  SubscribeSummaryDTO toSummaryDTO(Subscribe entity);

 // 조회 리스트(요약)
  List<SubscribeSummaryDTO> toSummaryDTOList(List<Subscribe> entities);


}