package com.imchobo.sayren_back.domain.order.mapper;
import com.imchobo.sayren_back.domain.order.dto.OrderDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderItemDTO;
import com.imchobo.sayren_back.domain.order.entity.Order;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  // Entity → DTO 변환
  @Mapping(source = "id", target = "orderId")
  @Mapping(source = "member.id", target = "memberId")
  @Mapping(source = "member.email", target = "memberEmail")
  @Mapping(source = "member.name", target = "memberName")
  @Mapping(source = "address.addrId", target = "addressId")
  @Mapping(source = "address.name", target = "addressName")
  @Mapping(source = "address.tel", target = "addressTel")
  @Mapping(source = "address.address", target = "addressDetail")
  OrderDTO toDTO(Order entity);





//  @Mapping(source = "member.memberId", target = "memberId")
//  @Mapping(source = "address.addrId", target = "addrId")
//  OrderDTO toDTO(Order entity); // 엔티티 → DTO
//
//  Order toEntity(OrderDTO dto); // DTO → 엔티티
//
//  OrderItemDTO toItemDTO(OrderItem entity); // 주문아이템 엔티티 → DTO
//
//  OrderItem toItemEntity(OrderItemDTO dto); // DTO → 엔티티
}