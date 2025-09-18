package com.imchobo.sayren_back.domain.address.mapper;

import com.imchobo.sayren_back.domain.address.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.address.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.address.entity.Address;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
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

  // ==== 보조 메서드 ====
  @Named("mapMember")
  default Member toMember(Long id) {
    if (id == null) return null;
    Member m = new Member();
    m.setId(id);
    return m;
  }
}
