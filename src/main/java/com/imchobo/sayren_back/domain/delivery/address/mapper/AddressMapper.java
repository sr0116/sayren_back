package com.imchobo.sayren_back.domain.delivery.address.mapper;

import com.imchobo.sayren_back.domain.delivery.address.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.address.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    // DTO → Entity 변환
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", source = "member")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "tel", source = "dto.tel")
    @Mapping(target = "zipcode", source = "dto.zipcode")
    @Mapping(target = "address", source = "dto.address")
    @Mapping(target = "isDefault", source = "dto.isDefault")
    @Mapping(target = "memo", source = "dto.memo")
    Address toEntity(AddressRequestDTO dto, Member member);

    // Entity → ResponseDTO 변환
    AddressResponseDTO toResponseDTO(Address entity);

    // Entity 리스트 → ResponseDTO 리스트 변환
    List<AddressResponseDTO> toResponseDTOs(List<Address> entities);

    // 수정용 (기존 Entity에 DTO 값 덮어쓰기)
    @Mapping(target = "member", ignore = true) // 수정 시 회원정보 변경 금지
    void updateEntityFromDto(AddressRequestDTO dto, @MappingTarget Address entity);
}
