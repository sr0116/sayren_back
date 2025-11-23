package com.imchobo.sayren_back.domain.member.mapper;

import com.imchobo.sayren_back.domain.member.dto.admin.MemberDetailTermResponseDTO;
import com.imchobo.sayren_back.domain.member.entity.MemberTerm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberTermMapper {
  @Mapping(target = "termType", source = "term.type")
  MemberDetailTermResponseDTO toMemberDetailResponseDTO(MemberTerm term);
}
