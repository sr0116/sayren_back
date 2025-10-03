package com.imchobo.sayren_back.domain.order.mapper;

import com.imchobo.sayren_back.domain.order.dto.OrderResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = "spring",
  uses = { OrderItemMapper.class },
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

  // Order → OrderResponseDTO
  @Mapping(source = "id", target = "orderId")                         // 주문 PK
  @Mapping(source = "status", target = "status")                      // 주문 상태

  // 회원 정보
  @Mapping(source = "member.email", target = "memberEmail")           // 회원 이메일
  @Mapping(source = "member.name", target = "memberName")             // 회원 이름

  // 배송지 정보
  @Mapping(source = "address.id", target = "addressId")               // 배송지 PK
  @Mapping(source = "address.name", target = "addressName")           // 수령인 이름
  @Mapping(source = "address.tel", target = "addressTel")             // 연락처
  @Mapping(source = "address.zipcode", target = "addressZipcode")     // 우편번호
  @Mapping(source = "address.address", target = "addressDetail")      // 상세주소
  @Mapping(source = "address.memo", target = "addressMemo")           // 배송 메모

  // 주문 아이템 리스트
  @Mapping(source = "orderItems", target = "orderItems")              // 주문 아이템 목록

//주문이력상태
  @Mapping(source = "histories", target = "histories")
  // 시간 정보
  @Mapping(source = "regDate", target = "regDate")                    // 생성일
  @Mapping(source = "modDate", target = "modDate")                    // 수정일
  OrderResponseDTO toResponseDTO(Order order);
}