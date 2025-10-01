package com.imchobo.sayren_back.domain.order.mapper;

import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  // Request DTO → Entity
  @Mapping(source = "addressId", target = "address.id")
  @Mapping(source = "status", target = "status")
  Order toEntity(OrderRequestDTO dto);

  // Entity → Response DTO
  @Mapping(source = "id", target = "orderId")
  @Mapping(source = "status", target = "status")

  // ===== 회원 정보 =====
  @Mapping(source = "member.email", target = "memberEmail")
  @Mapping(source = "member.name", target = "memberName")

  // ===== 배송지 정보 =====
  @Mapping(source = "address.id", target = "addressId")
  @Mapping(source = "address.name", target = "addressName")
  @Mapping(source = "address.tel", target = "addressTel")
  @Mapping(source = "address.address", target = "addressDetail")

  // ===== 시간 정보 =====
  @Mapping(source = "regDate", target = "regDate")
  @Mapping(source = "modDate", target = "modDate")

  OrderResponseDTO toResponseDTO(Order entity);
}