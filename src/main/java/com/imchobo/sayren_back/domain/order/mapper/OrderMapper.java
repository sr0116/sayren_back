package com.imchobo.sayren_back.domain.order.mapper;

import com.imchobo.sayren_back.domain.address.entity.Address;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.dto.OrderRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
// componentModel="spring" → 스프링 빈으로 등록돼서 @Autowired/@RequiredArgsConstructor로 주입 가능.
public interface OrderMapper {

  // ========================
  // Request DTO → Entity
  // ========================

  @Mapping(source = "memberId", target = "member.id")
  // DTO 안에 있는 memberId(Long) → Order 엔티티 안의 Member 객체의 id 필드로 매핑.
  // (MapStruct가 내부적으로 Member 객체 생성 후 id 주입)

  @Mapping(source = "addressId", target = "address.id")
  // DTO 안에 있는 addressId(Long) → Order 엔티티 안의 Address 객체의 id 필드로 매핑.

  @Mapping(source = "status", target = "status")
    // DTO 안에 있는 status(String) → Order 엔티티 안의 status(Enum).
    // Enum과 String 간 변환은 MapStruct가 자동 처리.

  Order toEntity(OrderRequestDTO dto);
  // 최종적으로 OrderRequestDTO → Order 엔티티 변환.

  // ========================
  // Entity → Response DTO
  // ========================

  @Mapping(source = "id", target = "orderId")
  //  Order.id (PK) → ResponseDTO.orderId

  @Mapping(source = "status", target = "status")
  //  Order.status (Enum) → ResponseDTO.status (String)

  // ===== 회원 정보 =====
  @Mapping(source = "member.id", target = "memberId")
  //  Order.member.id → ResponseDTO.memberId

  @Mapping(source = "member.email", target = "memberEmail")
  //  Order.member.email → ResponseDTO.memberEmail

  @Mapping(source = "member.name", target = "memberName")
  //  Order.member.name → ResponseDTO.memberName

  // ===== 배송지 정보 =====
  @Mapping(source = "address.id", target = "addressId")
  //  Order.address.id → ResponseDTO.addressId

  @Mapping(source = "address.name", target = "addressName")
  //  Order.address.name (수령인 이름) → ResponseDTO.addressName

  @Mapping(source = "address.tel", target = "addressTel")
  //  Order.address.tel (수령인 연락처) → ResponseDTO.addressTel

  @Mapping(source = "address.address", target = "addressDetail")
  //  Order.address.address (실제 주소 문자열) → ResponseDTO.addressDetail

  // ===== 시간 정보 =====
  @Mapping(source = "regDate", target = "regDate")
  //  BaseEntity.regDate → ResponseDTO.regDate

  @Mapping(source = "modDate", target = "modDate")
    // BaseEntity.modDate → ResponseDTO.modDate

  OrderResponseDTO toResponseDTO(Order entity);
  //  Order 엔티티 전체를 OrderResponseDTO로 변환.
}
