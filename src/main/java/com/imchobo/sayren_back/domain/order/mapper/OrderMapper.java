package com.imchobo.sayren_back.domain.order.mapper;

import com.imchobo.sayren_back.domain.delivery.entity.Address;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  // ========================
  // Request DTO → Entity
  // ========================
  @Mapping(source = "memberId", target = "member.id")
  @Mapping(source = "addressId", target = "address.id")
  @Mapping(source = "status", target = "status")
  Order toEntity(OrderRequestDTO dto);

  // ========================
  // Entity → Response DTO
  // ========================
  @Mapping(source = "id", target = "orderId")
  @Mapping(source = "status", target = "status")

  @Mapping(source = "member.id", target = "memberId")
  @Mapping(source = "member.email", target = "memberEmail")
  @Mapping(source = "member.name", target = "memberName")

  @Mapping(source = "address.id", target = "addressId")
  @Mapping(source = "address.name", target = "addressName")
  @Mapping(source = "address.tel", target = "addressTel")
  @Mapping(source = "address.address", target = "addressDetail")

  @Mapping(source = "regDate", target = "regDate")
  @Mapping(source = "modDate", target = "modDate")
  OrderResponseDTO toResponseDTO(Order entity);

  // ========================
  // 헬퍼 메서드 (필요 시)
  // ========================
  default Member toMember(Long memberId) {
    if (memberId == null) return null;
    Member m = new Member();
    m.setId(memberId);
    return m;
  }

  default Address toAddress(Long addressId) {
    if (addressId == null) return null;
    Address a = new Address();
    a.setId(addressId);
    return a;
  }
}
