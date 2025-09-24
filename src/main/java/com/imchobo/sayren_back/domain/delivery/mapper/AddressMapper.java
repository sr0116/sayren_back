package com.imchobo.sayren_back.domain.delivery.mapper;

import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import com.imchobo.sayren_back.domain.delivery.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Address;
import com.imchobo.sayren_back.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface AddressMapper {

  // DTO → Entity
  @Mapping(source = "memberId", target = "member", qualifiedByName = "mapMember")
  Address toEntity(AddressRequestDTO dto);

  // Entity → ResponseDTO
  @Mapping(source = "member.id", target = "memberId")
  AddressResponseDTO toResponseDTO(Address entity);

  List<AddressResponseDTO> toResponseDTOs(List<Address> entities);
}

