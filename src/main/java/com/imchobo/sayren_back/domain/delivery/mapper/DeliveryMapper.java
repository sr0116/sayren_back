package com.imchobo.sayren_back.domain.delivery.mapper;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;  // ✅ 반드시 명시적으로 import
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface DeliveryMapper {

    @Mappings({
      @Mapping(target = "member", source = "memberId", qualifiedByName = "mapMember"),
      @Mapping(target = "address", source = "addressId", qualifiedByName = "mapAddress"),
      @Mapping(target = "type", source = "type", qualifiedByName = "mapDeliveryType")
    })
    Delivery toEntity(DeliveryRequestDTO dto);

    @Mappings({
      @Mapping(target = "memberId", source = "member", qualifiedByName = "mapMemberId"),
      @Mapping(target = "addressId", source = "address", qualifiedByName = "mapAddressId"),
      @Mapping(target = "type", source = "type", qualifiedByName = "mapDeliveryTypeToString"),
      @Mapping(target = "status", source = "status", qualifiedByName = "mapDeliveryStatusToString")
    })
    DeliveryResponseDTO toResponseDTO(Delivery entity);
}
