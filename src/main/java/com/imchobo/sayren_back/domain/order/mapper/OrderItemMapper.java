package com.imchobo.sayren_back.domain.order.mapper;

import com.imchobo.sayren_back.domain.order.dto.OrderItemResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

  @Mapping(source = "id", target = "orderItemId")              // 주문아이템 PK
  @Mapping(source = "product.id", target = "productId")        // 상품 ID
  @Mapping(source = "product.name", target = "productName")    // 상품 이름
  @Mapping(source = "productPriceSnapshot", target = "priceSnapshot") // 가격 스냅샷
  @Mapping(source = "orderPlan.id", target = "planId")         // 요금제 ID
  @Mapping(source = "orderPlan.type", target = "planType")     // 요금제 타입
  OrderItemResponseDTO toResponseDTO(OrderItem entity);
}