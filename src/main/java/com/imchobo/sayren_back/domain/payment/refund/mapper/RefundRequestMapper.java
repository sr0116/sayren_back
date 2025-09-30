package com.imchobo.sayren_back.domain.payment.refund.mapper;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestDTO;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface RefundRequestMapper {

  //  DTO -> 엔티티
  @Mapping(target = "id", ignore = true) // PK 자동 생성
  @Mapping(source = "orderItemId", target = "orderItem", qualifiedByName = "mapOrderItem")
  @Mapping(target = "member", ignore = true)
  RefundRequest toEntity(RefundRequestDTO dto);

  // 엔티티 → 응답 DTO
  @Mapping(source = "id", target = "refundRequestId")
  @Mapping(source = "orderItem.product.name", target = "productName")
  @Mapping(source = "orderItem.orderPlan.type", target = "orderPlanType")
  RefundRequestResponseDTO toResponseDTO(RefundRequest entity);

  List<RefundRequestResponseDTO> toResponseDTOs(List<RefundRequest> entities);
}
