package com.imchobo.sayren_back.domain.delivery.mapper;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface DeliveryMapper {
    @Mapping(source = "id", target = "deliveryId")
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "address.id", target = "addressId")
    @Mapping(source = "type", target = "type", qualifiedByName = "mapDeliveryTypeToString")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapDeliveryStatusToString")
    DeliveryResponseDTO toResponseDTO(Delivery entity);
}
