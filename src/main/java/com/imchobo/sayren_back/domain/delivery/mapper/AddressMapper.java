package com.imchobo.sayren_back.domain.delivery.mapper;

import com.imchobo.sayren_back.domain.delivery.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  @Mapping(source = "memberId", target = "member.id")
  Address toEntity(AddressRequestDTO dto);

  @Mapping(source = "member.id", target = "memberId")
  AddressResponseDTO toResponseDTO(Address entity);

  List<AddressResponseDTO> toResponseDTOs(List<Address> entities);
}
