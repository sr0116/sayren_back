package com.imchobo.sayren_back.domain.order.mapper;

import com.imchobo.sayren_back.domain.order.dto.OrderHistoryResponseDTO;
import com.imchobo.sayren_back.domain.order.entity.OrderHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderHistoryMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "address", target = "address")
  @Mapping(source = "changedBy", target = "changedBy")
  @Mapping(source = "regDate", target = "regDate")
  OrderHistoryResponseDTO toResponseDTO(OrderHistory entity);
}
