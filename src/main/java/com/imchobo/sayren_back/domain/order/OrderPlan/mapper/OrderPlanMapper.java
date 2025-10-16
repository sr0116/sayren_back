package com.imchobo.sayren_back.domain.order.OrderPlan.mapper;

import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanRequestDTO;
import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanResponseDTO;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface OrderPlanMapper {


  @Mapping(target = "id", ignore = true)
  OrderPlan toEntity(OrderPlanRequestDTO dto);

  // Entity > ResponseDTO 변환 (조회 응답 시 사용)
  @Mapping(source = "id", target = "planId")
  OrderPlanResponseDTO toResponseDTO(OrderPlan entity);
  // Entity List > ResponseDTO List 변환 (목록 조회 시 사용)
  List<OrderPlanResponseDTO> toResponseDTOs(List<OrderPlan> entities);
}
