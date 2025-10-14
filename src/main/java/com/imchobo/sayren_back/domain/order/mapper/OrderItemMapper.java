package com.imchobo.sayren_back.domain.order.mapper;

import com.imchobo.sayren_back.domain.order.dto.OrderItemResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderItemMapper {

  @Mapping(source = "id", target = "orderItemId")
  @Mapping(source = "product.id", target = "productId")
  @Mapping(source = "product.name", target = "productName")
  @Mapping(source = "product.modelName", target = "modelName")
  @Mapping(source = "productPriceSnapshot", target = "priceSnapshot")
  @Mapping(source = "orderPlan.id", target = "planId")
  @Mapping(source = "orderPlan.type", target = "planType")
  OrderItemResponseDTO toResponseDTO(OrderItem entity);
}
