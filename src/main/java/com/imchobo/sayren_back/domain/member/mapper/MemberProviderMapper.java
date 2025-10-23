package com.imchobo.sayren_back.domain.member.mapper;

import com.imchobo.sayren_back.domain.member.dto.SocialResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.admin.MemberDetailProviderResponseDTO;
import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberProviderMapper {
  SocialResponseDTO toDTO(MemberProvider entity);

  MemberDetailProviderResponseDTO toMemberDetailResponseDTO(MemberProvider provider);
}
