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

  // DTO + Member → Entity
  @Mapping(target = "member", source = "member")
  @Mapping(target = "name", source = "dto.name")   //명시적으로 지정
  @Mapping(target = "tel", source = "dto.tel")
  @Mapping(target = "zipcode", source = "dto.zipcode")
  @Mapping(target = "address", source = "dto.address")
  @Mapping(target = "isDefault", source = "dto.isDefault")
  @Mapping(target = "memo", source = "dto.memo")
  Address toEntity(AddressRequestDTO dto, Member member);

  // Entity → ResponseDTO
  @Mapping(source = "member.id", target = "memberId")
  AddressResponseDTO toResponseDTO(Address entity);

  List<AddressResponseDTO> toResponseDTOs(List<Address> entities);
}
