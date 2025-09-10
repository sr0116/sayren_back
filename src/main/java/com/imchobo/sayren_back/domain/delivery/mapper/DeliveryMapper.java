package com.imchobo.sayren_back.domain.delivery.mapper;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
  //DTO -> Entity,  Entity -> DTO
  @Mapping(target = "items", ignore = true) // items는 DTO에서 매핑 안 함
  Delivery toEntity(DeliveryDTO dto);

  DeliveryDTO toDTO(Delivery entity);
}
