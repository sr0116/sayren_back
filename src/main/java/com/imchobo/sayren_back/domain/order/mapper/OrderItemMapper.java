package com.imchobo.sayren_back.domain.order.mapper;

import com.imchobo.sayren_back.domain.order.dto.OrderItemResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

  @Mapping(source = "id", target = "orderItemId")
  @Mapping(source = "product.id", target = "productId")
  @Mapping(source = "product.name", target = "productName")
  @Mapping(source = "productPriceSnapshot", target = "priceSnapshot")
  @Mapping(source = "orderPlan.id", target = "planId")
  @Mapping(source = "orderPlan.type", target = "planType") // 요금제 타입 추가
  OrderItemResponseDTO toResponseDTO(OrderItem entity);
}