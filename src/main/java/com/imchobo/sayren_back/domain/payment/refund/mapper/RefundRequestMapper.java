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
  @Mapping(target = "orderItem", ignore = true) // 서비스에서 주입
  @Mapping(target = "status", ignore = true)
  RefundRequest toEntity(RefundRequestDTO dto);

  // 엔티티 → 응답 DTO
  @Mapping(source = "id", target = "refundRequestId")
  @Mapping(source = "orderItem.product.name", target = "productName")
  @Mapping(source = "orderItem.orderPlan.type", target = "orderPlanType")
  @Mapping(source = "orderItem.id", target = "orderItemId")
  @Mapping(source = "member.name", target = "memberName")
  @Mapping(source = "member.email", target = "memberEmail")
  @Mapping(source = "orderItem.product", target = "productThumbnail",
          qualifiedByName = "mapProductThumbnailUrl")
  RefundRequestResponseDTO toResponseDTO(RefundRequest entity);

  List<RefundRequestResponseDTO> toResponseDTOs(List<RefundRequest> entities);
}
