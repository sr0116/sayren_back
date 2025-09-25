package com.imchobo.sayren_back.domain.order.mapper;

import com.imchobo.sayren_back.domain.order.dto.OrderPlanRequestDTO;
import com.imchobo.sayren_back.domain.order.dto.OrderPlanResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.OrderPlan;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface OrderPlanMapper {

  // RequestDTO → Entity 변환 (등록/수정 시 사용)
  @Mapping(target = "id", ignore = true) // 생성 시 PK는 무시
  OrderPlan toEntity(OrderPlanRequestDTO dto);

  // Entity → ResponseDTO 변환 (조회 응답 시 사용)
  @Mapping(source = "id", target = "planId")
  OrderPlanResponseDTO toResponseDTO(OrderPlan entity);
  // Entity List → ResponseDTO List 변환 (목록 조회 시 사용)
  List<OrderPlanResponseDTO> toResponseDTOs(List<OrderPlan> entities);
}
