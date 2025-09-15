package com.imchobo.sayren_back.domain.delivery.mapper;

import com.imchobo.sayren_back.domain.delivery.dto.AddressCreateRequest;
import com.imchobo.sayren_back.domain.delivery.dto.AddressCreateResponse;
import com.imchobo.sayren_back.domain.delivery.dto.AddressDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AddressMapper {


  // Entity → DTO
  AddressDTO toDTO(Address entity);

  // DTO → Entity
  Address toEntity(AddressDTO dto);

  // CreateRequest → Entity
  Address toEntity(AddressCreateRequest request);

  // Entity → CreateResponse
  AddressCreateResponse toCreateResponse(Address entity);
}
