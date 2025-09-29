package com.imchobo.sayren_back.domain.order.cart.mapper;
import com.imchobo.sayren_back.domain.order.cart.dto.CartItemResponseDTO;
import com.imchobo.sayren_back.domain.order.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

  @Mapping(source = "id", target = "cartItemId")
  @Mapping(source = "product.id", target = "productId")
  @Mapping(source = "product.name", target = "productName")
  @Mapping(source = "orderPlan.id", target = "planId")
  @Mapping(source = "orderPlan.type", target = "planType")
  @Mapping(source = "quantity", target = "quantity")
  @Mapping(source = "product.price", target = "price")
  CartItemResponseDTO toResponseDTO(CartItem entity);
}