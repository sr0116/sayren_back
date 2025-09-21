package com.imchobo.sayren_back.domain.payment.refund.mapper;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundCreateDTO;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtil.class})
public interface RefundMapper {

  //  ㄹDTO 요청엔티티
  @Mapping(target = "id", ignore = true)   // PK 자동 생성
  @Mapping(source = "paymentId", target = "payment", qualifiedByName = "mapPayment")
  Refund toEntity(RefundCreateDTO dto);

  List<Refund> toEntities(List<RefundCreateDTO> dtos);

  // 엔티티 → 응답 DTO
  @Mapping(source = "id", target = "refundId")
  @Mapping(source = "payment.id", target = "paymentId")
  RefundResponseDTO toDto(Refund entity);

  List<RefundResponseDTO> toResponseDTOs(List<Refund> entities);
}
