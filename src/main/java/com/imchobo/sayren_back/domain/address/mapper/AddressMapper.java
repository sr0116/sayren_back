package com.imchobo.sayren_back.domain.address.mapper;

import com.imchobo.sayren_back.domain.address.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.address.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.address.entity.Address;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface AddressMapper {

  // ========================
  // Request DTO → Entity
  // ========================
  @Mapping(source = "memberId", target = "member", qualifiedByName = "mapMember")
  Address toEntity(AddressRequestDTO dto);

  // ========================
  // Entity → Response DTO
  // ========================
  @Mapping(source = "member.id", target = "memberId")
  AddressResponseDTO toResponseDTO(Address entity);

  List<AddressResponseDTO> toResponseDTOs(List<Address> entities);
}
